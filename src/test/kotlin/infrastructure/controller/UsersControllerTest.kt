package infrastructure.controller

import age.of.printscript.demo.application.query.SearchUsersUseCase
import age.of.printscript.demo.domain.DirectoryUser
import age.of.printscript.demo.domain.UserPage
import age.of.printscript.demo.domain.port.UserDirectory
import age.of.printscript.demo.domain.port.UserDirectoryUnavailableException
import age.of.printscript.demo.infrastructure.controller.PaginatedUsersResponse
import age.of.printscript.demo.infrastructure.controller.UserSummaryResponse
import age.of.printscript.demo.infrastructure.controller.UsersController
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersControllerTest {
    private fun jwt(subject: String): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "none")
            .subject(subject)
            .build()

    @Test
    fun `search returns 200 with paginated users`() {
        val dir =
            object : UserDirectory {
                override fun search(
                    query: String?,
                    page: Int,
                    pageSize: Int,
                    excludeUserId: String?,
                ) = UserPage(0, 5, 1, listOf(DirectoryUser("auth0|2", "pepe1 (pepe1@mail.com)")))
            }
        val controller = UsersController(SearchUsersUseCase(dir))

        val response = controller.search("pepe", 0, 5, jwt("auth0|1"))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            PaginatedUsersResponse(
                page = 0,
                pageSize = 5,
                count = 1,
                users = listOf(UserSummaryResponse("auth0|2", "pepe1 (pepe1@mail.com)")),
            ),
            response.body,
        )
    }

    @Test
    fun `search returns 503 when directory is unavailable`() {
        val dir =
            object : UserDirectory {
                override fun search(
                    query: String?,
                    page: Int,
                    pageSize: Int,
                    excludeUserId: String?,
                ): UserPage = throw UserDirectoryUnavailableException("missing creds")
            }
        val controller = UsersController(SearchUsersUseCase(dir))

        val response = controller.search("pepe", 0, 5, jwt("auth0|1"))

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals(ErrorResponse("user_directory_unavailable"), response.body)
    }
}
