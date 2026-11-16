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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets/{snippetId}/members")
class SnippetMembersController(
    private val manageMembers: ManageSnippetMembersService,
) {
    private val log = LoggerFactory.getLogger(SnippetMembersController::class.java)

    @PostMapping
    fun share(
        @PathVariable snippetId: String,
        @Valid @RequestBody body: ShareSnippetRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Any> =
        when (manageMembers.share(jwt.subject!!, body.userId, snippetId, body.role)) {
            ShareResult.Success -> {
                log.info("Shared snippet {} with user {} with role {}", snippetId, body.userId, body.role)
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MemberResponse(snippetId, body.userId, body.role.trim().uppercase()))
            }

            ShareResult.NotAllowed -> {
                log.warn("Denied sharing snippet {} with user {}: requester is not allowed", snippetId, body.userId)
                error(HttpStatus.FORBIDDEN, "not_allowed")
            }

            ShareResult.AlreadyMember -> {
                log.warn("Cannot share snippet {} with user {}: already a member", snippetId, body.userId)
                error(HttpStatus.CONFLICT, "already_member")
            }

            ShareResult.RoleNotFound -> {
                log.warn("Cannot share snippet {} with user {}: role {} not found", snippetId, body.userId, body.role)
                error(HttpStatus.UNPROCESSABLE_ENTITY, "role_not_found")
            }

            ShareResult.OwnerRoleNotAssignable -> {
                log.warn(
                    "Cannot share snippet {} with user {}: OWNER role cannot be assigned via share",
                    snippetId,
                    body.userId,
                )
                error(HttpStatus.UNPROCESSABLE_ENTITY, "cannot_assign_owner")
            }
        }

    @PutMapping("/{targetUserId}")
    fun changeRole(
        @PathVariable snippetId: String,
        @PathVariable targetUserId: String,
        @Valid @RequestBody body: ChangeRoleRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Any> =
        when (manageMembers.changeRole(jwt.subject!!, targetUserId, snippetId, body.role)) {
            ChangeRoleResult.Success -> {
                log.info("Changed role of user {} on snippet {} to {}", targetUserId, snippetId, body.role)
                ResponseEntity.ok(MemberResponse(snippetId, targetUserId, body.role.trim().uppercase()))
            }

            ChangeRoleResult.NotAllowed -> {
                log.warn(
                    "Denied role change for user {} on snippet {}: requester is not allowed",
                    targetUserId,
                    snippetId,
                )
                error(HttpStatus.FORBIDDEN, "not_allowed")
            }

            ChangeRoleResult.CannotModifySelf -> {
                log.warn(
                    "Cannot change role for user {} on snippet {}: users cannot modify their own role",
                    targetUserId,
                    snippetId,
                )
                error(HttpStatus.CONFLICT, "cannot_modify_self")
            }

            ChangeRoleResult.MemberNotFound -> {
                log.warn("Cannot change role on snippet {}: user {} is not a member", snippetId, targetUserId)
                error(HttpStatus.NOT_FOUND, "member_not_found")
            }

            ChangeRoleResult.RoleNotFound -> {
                log.warn(
                    "Cannot change role for user {} on snippet {}: role {} not found",
                    targetUserId,
                    snippetId,
                    body.role,
                )
                error(HttpStatus.UNPROCESSABLE_ENTITY, "role_not_found")
            }

            ChangeRoleResult.OwnerRoleNotAssignable -> {
                log.warn(
                    "Cannot change role for user {} on snippet {}: cannot assign OWNER role",
                    targetUserId,
                    snippetId,
                )
                error(HttpStatus.UNPROCESSABLE_ENTITY, "cannot_assign_owner")
            }
        }

    @DeleteMapping("/{targetUserId}")
    fun revoke(
        @PathVariable snippetId: String,
        @PathVariable targetUserId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Any> =
        when (manageMembers.revoke(jwt.subject!!, targetUserId, snippetId)) {
            RevokeResult.Success -> {
                log.info("Revoked membership for user {} on snippet {}", targetUserId, snippetId)
                ResponseEntity.noContent().build()
            }

            RevokeResult.NotAllowed -> {
                log.warn(
                    "Denied membership revocation for user {} on snippet {}: requester is not allowed",
                    targetUserId,
                    snippetId,
                )
                error(HttpStatus.FORBIDDEN, "not_allowed")
            }

            RevokeResult.CannotModifySelf -> {
                log.warn(
                    "Cannot revoke membership on snippet {}: users cannot revoke their own membership",
                    snippetId,
                )
                error(HttpStatus.CONFLICT, "cannot_modify_self")
            }

            RevokeResult.MemberNotFound -> {
                log.warn("Cannot revoke membership on snippet {}: user {} is not a member", snippetId, targetUserId)
                error(HttpStatus.NOT_FOUND, "member_not_found")
            }
        }

    private fun error(
        status: HttpStatus,
        code: String,
    ): ResponseEntity<Any> = ResponseEntity.status(status).body(ErrorResponse(code))
}
