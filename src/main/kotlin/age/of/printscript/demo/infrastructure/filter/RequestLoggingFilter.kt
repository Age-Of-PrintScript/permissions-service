package age.of.printscript.demo.infrastructure.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * Filtro perimetral que intercepta todas las peticiones HTTP entrantes con la máxima prioridad.
 *
 * Responsabilidades principales:
 * 1. Correlation ID: Lee el header "X-Correlation-Id" (si el request fue iniciado por otro microservicio,
 *    por ejemplo snippet-service) o genera un UUID nuevo si el request vino directamente del cliente.
 * 2. Carga inicial en MDC: Registra "correlation-id" y "service" en el contexto de logging para que
 *    cualquier línea escrita por este hilo incluya estos metadatos automáticamente.
 * 3. Header de respuesta: Devuelve el "X-Correlation-Id" al cliente para que pueda rastrear su operación.
 * 4. Métricas de latencia y resultado: Mide la duración total en ms y loguea el resultado al finalizar:
 *    - INFO (2xx / 3xx): operación normal.
 *    - WARN (4xx): error del cliente (ej. 404 not found, 403 forbidden, 409 conflict).
 *    - ERROR (5xx): fallo inesperado del servidor o base de datos.
 * 5. Limpieza de hilo: Ejecuta MDC.clear() en el bloque `finally` para evitar que queden datos residuales
 *    en los hilos reutilizados del pool de Tomcat.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val CORRELATION_ID_KEY = "correlation-id"
        const val SERVICE_KEY = "service"
        const val SERVICE_NAME = "permissions-service"

        private const val HTTP_SERVER_ERROR_THRESHOLD = 500
        private const val HTTP_CLIENT_ERROR_THRESHOLD = 400
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 1. Obtener o generar el Correlation ID para seguir la operación entre microservicios
        val correlationId =
            request.getHeader(CORRELATION_ID_HEADER)?.takeIf { it.isNotBlank() }
                ?: UUID.randomUUID().toString()

        // 2. Cargar el contexto en MDC y propagar el header en la respuesta
        MDC.put(CORRELATION_ID_KEY, correlationId)
        MDC.put(SERVICE_KEY, SERVICE_NAME)
        response.setHeader(CORRELATION_ID_HEADER, correlationId)

        val start = System.currentTimeMillis()
        try {
            // Continuar con la cadena de filtros y ejecución del controlador
            filterChain.doFilter(request, response)
        } finally {
            // 3. Al finalizar el request (incluso si hubo excepción), medimos tiempo y logueamos el resultado
            val duration = System.currentTimeMillis() - start
            val status = response.status
            val method = request.method
            val uri = request.requestURI
            val message = "$method $uri - $status (${duration}ms)"

            when {
                status >= HTTP_SERVER_ERROR_THRESHOLD -> log.error(message)
                status >= HTTP_CLIENT_ERROR_THRESHOLD -> log.warn(message)
                else -> log.info(message)
            }

            // 4. Limpiar el MDC del hilo actual para no filtrar datos a futuros requests
            MDC.clear()
        }
    }
}
