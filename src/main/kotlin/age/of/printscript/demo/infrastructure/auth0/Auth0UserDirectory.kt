package age.of.printscript.demo.infrastructure.auth0

import age.of.printscript.demo.domain.DirectoryUser
import age.of.printscript.demo.domain.UserPage
import age.of.printscript.demo.domain.port.UserDirectory
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class Auth0UserDirectory(
    private val restClient: RestClient,
    private val tokenProvider: Auth0ManagementTokenProvider,
) : UserDirectory {
    @Autowired
    constructor(
        restClientBuilder: RestClient.Builder,
        @Value("\${auth0.domain}") domain: String,
        tokenProvider: Auth0ManagementTokenProvider,
    ) : this(
        restClient = restClientBuilder.baseUrl("https://$domain").build(),
        tokenProvider = tokenProvider,
    )

    override fun search(
        query: String?,
        page: Int,
        pageSize: Int,
        excludeUserId: String?,
    ): UserPage {
        val luceneQuery = buildLuceneQuery(query, excludeUserId)
        val response =
            try {
                executeSearch(tokenProvider.getToken(), luceneQuery, page, pageSize)
            } catch (_: HttpClientErrorException.Unauthorized) {
                // Token pudo haber sido revocado antes de expirar: invalidar cache y reintentar una vez.
                tokenProvider.invalidate()
                executeSearch(tokenProvider.getToken(), luceneQuery, page, pageSize)
            }

        val users =
            response.users.map { u ->
                DirectoryUser(
                    id = u.userId,
                    name = formatDisplayName(u),
                )
            }

        return UserPage(
            page = page,
            pageSize = pageSize,
            total = response.total,
            users = users,
        )
    }

    private fun executeSearch(
        token: String,
        luceneQuery: String?,
        page: Int,
        pageSize: Int,
    ): Auth0UsersResponse =
        restClient
            .get()
            .uri { builder ->
                builder
                    .path("/api/v2/users")
                    .queryParam("search_engine", "v3")
                    .queryParam("page", page)
                    .queryParam("per_page", pageSize)
                    .queryParam("include_totals", true)
                    .queryParam("fields", "user_id,name,email,nickname")
                if (luceneQuery != null) {
                    builder.queryParam("q", luceneQuery)
                }
                builder.build()
            }.headers { it.setBearerAuth(token) }
            .retrieve()
            .body<Auth0UsersResponse>()
            ?: Auth0UsersResponse()

    private fun formatDisplayName(u: Auth0UserItem): String {
        val nickname = u.nickname?.takeIf { it.isNotBlank() }
        val email = u.email?.takeIf { it.isNotBlank() }
        val name = u.name?.takeIf { it.isNotBlank() }
        return when {
            nickname != null && email != null -> "$nickname ($email)"
            name != null -> name
            email != null -> email
            else -> u.userId
        }
    }

    internal fun buildLuceneQuery(
        query: String?,
        excludeUserId: String?,
    ): String? {
        val parts = mutableListOf<String>()
        if (!query.isNullOrBlank()) {
            val safe = escapeLucene(query.trim())
            parts += "(name:*$safe* OR email:*$safe* OR nickname:*$safe*)"
        }
        if (!excludeUserId.isNullOrBlank()) {
            val safeId = excludeUserId.replace("\\", "\\\\").replace("\"", "\\\"")
            parts += "NOT user_id:\"$safeId\""
        }
        return if (parts.isEmpty()) null else parts.joinToString(" AND ")
    }

    private fun escapeLucene(input: String): String = input.replace(Regex("""([+\-&|!(){}\[\]^"~*?:\\/])""")) { "\\${it.value}" }

    internal data class Auth0UsersResponse(
        val total: Long = 0,
        val users: List<Auth0UserItem> = emptyList(),
    )

    internal data class Auth0UserItem(
        @JsonProperty("user_id") val userId: String = "",
        val name: String? = null,
        val email: String? = null,
        val nickname: String? = null,
    )
}
