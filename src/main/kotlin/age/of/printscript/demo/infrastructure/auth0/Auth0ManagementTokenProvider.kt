package age.of.printscript.demo.infrastructure.auth0

import age.of.printscript.demo.domain.port.UserDirectoryUnavailableException
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.Clock
import java.time.Instant
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class Auth0ManagementTokenProvider(
    private val restClient: RestClient,
    private val domain: String,
    private val clientId: String,
    private val clientSecret: String,
    private val clock: Clock = Clock.systemUTC(),
) {
    @Autowired
    constructor(
        restClientBuilder: RestClient.Builder,
        @Value("\${auth0.domain}") domain: String,
        @Value("\${auth0.management.client-id:}") clientId: String,
        @Value("\${auth0.management.client-secret:}") clientSecret: String,
    ) : this(
        restClient = restClientBuilder.baseUrl("https://$domain").build(),
        domain = domain,
        clientId = clientId,
        clientSecret = clientSecret,
    )

    private val lock = ReentrantLock()
    private var cachedToken: String? = null
    private var expiresAt: Instant = Instant.EPOCH

    fun getToken(): String =
        lock.withLock {
            val now = clock.instant()
            val token = cachedToken
            if (token != null && now.isBefore(expiresAt)) {
                return@withLock token
            }
            fetchAndCacheToken(now)
        }

    fun invalidate() {
        lock.withLock {
            cachedToken = null
            expiresAt = Instant.EPOCH
        }
    }

    private fun fetchAndCacheToken(now: Instant): String {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            throw UserDirectoryUnavailableException("Auth0 Management API credentials are not configured")
        }

        val response =
            restClient
                .post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    TokenRequest(
                        clientId = clientId,
                        clientSecret = clientSecret,
                        audience = "https://$domain/api/v2/",
                    ),
                ).retrieve()
                .body<TokenResponse>()
                ?: throw UserDirectoryUnavailableException("Empty token response from Auth0")

        val ttl = (response.expiresIn - EXPIRY_SKEW_SECONDS).coerceAtLeast(0)
        cachedToken = response.accessToken
        expiresAt = now.plusSeconds(ttl)
        return response.accessToken
    }

    internal data class TokenRequest(
        @JsonProperty("client_id") val clientId: String,
        @JsonProperty("client_secret") val clientSecret: String,
        val audience: String,
        @JsonProperty("grant_type") val grantType: String = "client_credentials",
    )

    internal data class TokenResponse(
        @JsonProperty("access_token") val accessToken: String = "",
        @JsonProperty("expires_in") val expiresIn: Long = 0,
    )

    companion object {
        private const val EXPIRY_SKEW_SECONDS = 60L
    }
}
