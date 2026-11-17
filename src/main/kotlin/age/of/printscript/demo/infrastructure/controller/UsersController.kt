package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.query.SearchUsersUseCase
import age.of.printscript.demo.domain.port.UserDirectoryUnavailableException
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class UserSummaryResponse(
    val id: String,
    val name: String,
)

data class PaginatedUsersResponse(
    val page: Int,
    @JsonProperty("page_size")
    val pageSize: Int,
    val count: Long,
    val users: List<UserSummaryResponse>,
)

@RestController
@RequestMapping("/users")
class UsersController(
    private val searchUsers: SearchUsersUseCase,
) {
    @GetMapping
    fun search(
        @RequestParam(required = false) name: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Any> =
        try {
            val result = searchUsers(query = name, page = page, pageSize = pageSize, requesterId = jwt.subject)
            ResponseEntity.ok(
                PaginatedUsersResponse(
                    page = result.page,
                    pageSize = result.pageSize,
                    count = result.total,
                    users = result.users.map { UserSummaryResponse(id = it.id, name = it.name) },
                ),
            )
        } catch (_: UserDirectoryUnavailableException) {
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorResponse("user_directory_unavailable"))
        }
}
