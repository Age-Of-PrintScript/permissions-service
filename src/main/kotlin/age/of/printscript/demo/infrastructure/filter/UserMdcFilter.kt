package age.of.printscript.demo.infrastructure.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filtro que corre inmediatamente DESPUÉS de que Spring Security valida el Bearer JWT
 * (BearerTokenAuthenticationFilter).
 *
 * Propósito:
 * Extrae el identificador del usuario autenticado (subject del JWT) y lo agrega al MDC
 * bajo la clave "user-id".
 *
 * De este modo, cualquier log generado en los controladores o casos de uso llevará
 * automáticamente el ID del usuario en el `%mdc`, sin tener que pasarlo como argumento
 * en cada llamada a `logger.info(...)`.
 */
class UserMdcFilter : OncePerRequestFilter() {
    companion object {
        const val USER_ID_KEY = "user-id"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // Obtenemos la autenticación que Spring Security acaba de validar en el hilo actual
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = auth?.principal as? Jwt
        if (jwt != null && !jwt.subject.isNullOrBlank()) {
            MDC.put(USER_ID_KEY, jwt.subject)
        }

        // Continuar con el resto de la cadena hacia el controlador
        filterChain.doFilter(request, response)
    }
}
