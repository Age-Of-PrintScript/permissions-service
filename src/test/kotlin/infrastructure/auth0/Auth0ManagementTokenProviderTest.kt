package infrastructure.auth0

import age.of.printscript.demo.domain.port.UserDirectoryUnavailableException
import age.of.printscript.demo.infrastructure.auth0.Auth0ManagementTokenProvider
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Auth0ManagementTokenProviderTest {
    private class MutableClock(
        var current: Instant,
    ) : Clock() {
        override fun getZone() = ZoneOffset.UTC

        override fun withZone(zone: java.time.ZoneId?) = this

        override fun instant() = current
    }

    @Test
    fun `fetches token lazily, caches it, and refreshes after expiry`() {
        val builder = RestClient.builder().baseUrl("https://test.auth0.com")
        val server = MockRestServiceServer.bindTo(builder).build()
        val clock = MutableClock(Instant.parse("2026-11-16T12:00:00Z"))
        val provider = Auth0ManagementTokenProvider(builder.build(), "test.auth0.com", "cid", "csec", clock)

        server
            .expect(requestTo("https://test.auth0.com/oauth/token"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"access_token":"tok-1","expires_in":3600}""", MediaType.APPLICATION_JSON))

        assertEquals("tok-1", provider.getToken())
        // Segunda llamada dentro de TTL: usa cache, no pega a Auth0.
        clock.current = clock.current.plusSeconds(100)
        assertEquals("tok-1", provider.getToken())
        server.verify()
        server.reset()

        server
            .expect(requestTo("https://test.auth0.com/oauth/token"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"access_token":"tok-2","expires_in":3600}""", MediaType.APPLICATION_JSON))

        clock.current = clock.current.plusSeconds(3500)
        assertEquals("tok-2", provider.getToken())
        server.verify()
    }

    @Test
    fun `throws UserDirectoryUnavailableException when credentials are missing`() {
        val provider = Auth0ManagementTokenProvider(RestClient.create(), "test.auth0.com", "", "")
        assertFailsWith<UserDirectoryUnavailableException> { provider.getToken() }
    }
}
