package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.query.GetUserSnippetsUseCase
import age.of.printscript.demo.infrastructure.controller.dto.AccessibleSnippetResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** Endpoint interno (no expuesto por nginx): lo consulta snippet-service para listar propios y compartidos. */
@RestController
class UserSnippetsController(
    private val getUserSnippets: GetUserSnippetsUseCase,
) {
    @GetMapping("/snippets")
    fun list(
        @AuthenticationPrincipal jwt: Jwt,
    ): List<AccessibleSnippetResponse> =
        getUserSnippets(jwt.subject!!).map {
            AccessibleSnippetResponse(snippetId = it.snippetId, role = it.snippetRole.name)
        }
}
