package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.query.GetUserSnippetsUseCase
import age.of.printscript.demo.infrastructure.controller.dto.AccessibleSnippetResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** Endpoint interno (no expuesto por nginx): lo consulta snippet-service para listar propios y compartidos. */
@RestController
class UserSnippetsController(
    private val getUserSnippets: GetUserSnippetsUseCase,
) {
    private val log = LoggerFactory.getLogger(UserSnippetsController::class.java)

    @GetMapping("/snippets")
    fun list(
        @AuthenticationPrincipal jwt: Jwt,
    ): List<AccessibleSnippetResponse> {
        val snippets = getUserSnippets(jwt.subject!!)
        log.info("Listed {} accessible snippets for user {}", snippets.size, jwt.subject)
        return snippets.map {
            AccessibleSnippetResponse(snippetId = it.snippetId, role = it.snippetRole.name)
        }
    }
}
