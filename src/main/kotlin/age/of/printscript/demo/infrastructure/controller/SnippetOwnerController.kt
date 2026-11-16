package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.command.RegisterOwnerResult
import age.of.printscript.demo.application.command.RegisterSnippetOwnerUseCase
import age.of.printscript.demo.domain.RoleNames
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import age.of.printscript.demo.infrastructure.controller.dto.MemberResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Endpoint interno (no se expone por nginx): lo llama snippet-service al crear un snippet. */
@RestController
@RequestMapping("/snippets/{snippetId}/owner")
class SnippetOwnerController(
    private val registerOwner: RegisterSnippetOwnerUseCase,
) {
    private val log = LoggerFactory.getLogger(SnippetOwnerController::class.java)

    @PostMapping
    fun register(
        @PathVariable snippetId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Any> =
        when (registerOwner(jwt.subject!!, snippetId)) {
            RegisterOwnerResult.Success -> {
                log.info("Registered owner for snippet {}", snippetId)
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MemberResponse(snippetId, jwt.subject!!, RoleNames.OWNER))
            }

            RegisterOwnerResult.AlreadyHasOwner -> {
                log.warn("Failed to register owner for snippet {}: already has an owner", snippetId)
                ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse("already_has_owner"))
            }

            RegisterOwnerResult.OwnerRoleMissing -> {
                log.error("Failed to register owner for snippet {}: owner role missing in database", snippetId)
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse("owner_role_missing"))
            }
        }
}
