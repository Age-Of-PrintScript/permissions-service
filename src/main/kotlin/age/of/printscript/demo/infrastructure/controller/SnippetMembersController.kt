package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.ChangeRoleResult
import age.of.printscript.demo.application.ManageSnippetMembersService
import age.of.printscript.demo.application.RevokeResult
import age.of.printscript.demo.application.ShareResult
import age.of.printscript.demo.infrastructure.controller.dto.ChangeRoleRequest
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import age.of.printscript.demo.infrastructure.controller.dto.MemberResponse
import age.of.printscript.demo.infrastructure.controller.dto.ShareSnippetRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO: reemplazar el header X-User-Id por @AuthenticationPrincipal jwt: Jwt (jwt.subject)
// cuando veamos autenticación.
private const val REQUESTER_HEADER = "X-User-Id"

@RestController
@RequestMapping("/snippets/{snippetId}/members")
class SnippetMembersController(
    private val manageMembers: ManageSnippetMembersService,
) {
    @PostMapping
    fun share(
        @PathVariable snippetId: String,
        @Valid @RequestBody body: ShareSnippetRequest,
        @RequestHeader(REQUESTER_HEADER) requesterId: String,
    ): ResponseEntity<Any> =
        when (manageMembers.share(requesterId, body.userId, snippetId, body.roleId)) {
            ShareResult.Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MemberResponse(snippetId, body.userId, body.roleId))

            ShareResult.NotAllowed -> error(HttpStatus.FORBIDDEN, "not_allowed")

            ShareResult.AlreadyMember -> error(HttpStatus.CONFLICT, "already_member")

            ShareResult.RoleNotFound -> error(HttpStatus.UNPROCESSABLE_ENTITY, "role_not_found")
        }

    @PutMapping("/{targetUserId}")
    fun changeRole(
        @PathVariable snippetId: String,
        @PathVariable targetUserId: String,
        @Valid @RequestBody body: ChangeRoleRequest,
        @RequestHeader(REQUESTER_HEADER) requesterId: String,
    ): ResponseEntity<Any> =
        when (manageMembers.changeRole(requesterId, targetUserId, snippetId, body.roleId)) {
            ChangeRoleResult.Success ->
                ResponseEntity.ok(MemberResponse(snippetId, targetUserId, body.roleId))

            ChangeRoleResult.NotAllowed -> error(HttpStatus.FORBIDDEN, "not_allowed")

            ChangeRoleResult.CannotModifySelf -> error(HttpStatus.CONFLICT, "cannot_modify_self")

            ChangeRoleResult.MemberNotFound -> error(HttpStatus.NOT_FOUND, "member_not_found")

            ChangeRoleResult.RoleNotFound -> error(HttpStatus.UNPROCESSABLE_ENTITY, "role_not_found")
        }

    @DeleteMapping("/{targetUserId}")
    fun revoke(
        @PathVariable snippetId: String,
        @PathVariable targetUserId: String,
        @RequestHeader(REQUESTER_HEADER) requesterId: String,
    ): ResponseEntity<Any> =
        when (manageMembers.revoke(requesterId, targetUserId, snippetId)) {
            RevokeResult.Success -> ResponseEntity.noContent().build()

            RevokeResult.NotAllowed -> error(HttpStatus.FORBIDDEN, "not_allowed")

            RevokeResult.CannotModifySelf -> error(HttpStatus.CONFLICT, "cannot_modify_self")

            RevokeResult.MemberNotFound -> error(HttpStatus.NOT_FOUND, "member_not_found")
        }

    private fun error(
        status: HttpStatus,
        code: String,
    ): ResponseEntity<Any> = ResponseEntity.status(status).body(ErrorResponse(code))
}
