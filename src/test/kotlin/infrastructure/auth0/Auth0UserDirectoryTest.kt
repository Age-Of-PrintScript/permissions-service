package infrastructure.auth0

import age.of.printscript.demo.infrastructure.auth0.Auth0ManagementTokenProvider
import age.of.printscript.demo.infrastructure.auth0.Auth0UserDirectory
import org.hamcrest.Matchers.containsString
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import kotlin.test.Test
import kotlin.test.assertEquals

class Auth0UserDirectoryTest {
    @Test
    fun `queries auth0 users with sanitized query, exclusion and maps results`() {
        val tokenBuilder = RestClient.builder().baseUrl("https://test.auth0.com")
        val tokenServer = MockRestServiceServer.bindTo(tokenBuilder).build()
        val dirBuilder = RestClient.builder().baseUrl("https://test.auth0.com")
        val dirServer = MockRestServiceServer.bindTo(dirBuilder).build()

        val tokenProvider = Auth0ManagementTokenProvider(tokenBuilder.build(), "test.auth0.com", "cid", "csec")

        tokenServer
            .expect(requestTo("https://test.auth0.com/oauth/token"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"access_token":"m2m-tok","expires_in":3600}""", MediaType.APPLICATION_JSON))

        dirServer
            .expect(requestTo(containsString("/api/v2/users")))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer m2m-tok"))
            .andExpect(queryParam("search_engine", "v3"))
            .andExpect(queryParam("page", "0"))
            .andExpect(queryParam("per_page", "5"))
            .andExpect(queryParam("include_totals", "true"))
            .andRespond(
                withSuccess(
                    """
                    {
                      "total": 1,
                      "users": [
                        {"user_id":"auth0|2","email":"pepe1@mail.com","nickname":"pepe1","name":"pepe1@mail.com"}
                      ]
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val directory = Auth0UserDirectory(dirBuilder.build(), tokenProvider)
        val page = directory.search(query = "pepe*", page = 0, pageSize = 5, excludeUserId = "auth0|1")

        assertEquals(1, page.total)
        assertEquals(1, page.users.size)
        assertEquals("auth0|2", page.users.first().id)
        assertEquals("pepe1 (pepe1@mail.com)", page.users.first().name)
        tokenServer.verify()
        dirServer.verify()
    }
}
