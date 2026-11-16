package age.of.printscript.demo.infrastructure.configuration

import age.of.printscript.demo.infrastructure.filter.UserMdcFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

/**
 * Resource server de Auth0: todos los requests requieren un Bearer JWT válido.
 * La validación (firma vía JWKS, exp/nbf, iss y aud) se configura en application.properties
 * mediante spring.security.oauth2.resourceserver.jwt.*, por lo que no hace falta un JwtDecoder propio.
 */
@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
            // Inyecta el user-id (subject del JWT) en el MDC inmediatamente después de autenticar el token
            .addFilterAfter(UserMdcFilter(), BearerTokenAuthenticationFilter::class.java)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .build()
}
