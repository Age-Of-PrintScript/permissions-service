package age.of.printscript.demo.infrastructure.filter

import jakarta.servlet.FilterChain
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.Instant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserMdcFilterTest {
    private val filter = UserMdcFilter()

    @AfterTest
    fun tearDown() {
        SecurityContextHolder.clearContext()
        MDC.clear()
    }

    @Test
    fun `sets user-id in MDC when user is authenticated with Jwt`() {
        val jwt =
            Jwt(
                "dummy-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                mapOf("alg" to "none"),
                mapOf("sub" to "auth0|test-user-123"),
            )
        val auth = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = auth

        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        var capturedUserId: String? = null
        val chain =
            FilterChain { _, _ ->
                capturedUserId = MDC.get(UserMdcFilter.USER_ID_KEY)
            }

        filter.doFilter(request, response, chain)

        assertEquals("auth0|test-user-123", capturedUserId)
    }

    @Test
    fun `does not set user-id in MDC when authentication is absent`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        var capturedUserId: String? = null
        val chain =
            FilterChain { _, _ ->
                capturedUserId = MDC.get(UserMdcFilter.USER_ID_KEY)
            }

        filter.doFilter(request, response, chain)

        assertNull(capturedUserId)
    }
}
