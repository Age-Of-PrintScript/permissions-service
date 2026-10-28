package age.of.printscript.demo.application

import age.of.printscript.demo.application.command.AddSnippetMemberUseCase
import age.of.printscript.demo.application.command.CreateUserPermissionResult
import age.of.printscript.demo.application.command.DeleteUserPermissionResult
import age.of.printscript.demo.application.command.RemoveSnippetMemberUseCase
import age.of.printscript.demo.application.command.UpdateUserRoleResult
import age.of.printscript.demo.application.command.UpdateUserRoleUseCase
import age.of.printscript.demo.application.query.CheckUserPermissionUseCase
import age.of.printscript.demo.domain.Permissions
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

sealed interface ChangeRoleResult {
    data object Success : ChangeRoleResult

    data object NotAllowed : ChangeRoleResult

    data object CannotModifySelf : ChangeRoleResult

    data object MemberNotFound : ChangeRoleResult

    data object RoleNotFound : ChangeRoleResult
}

sealed interface ShareResult {
    data object Success : ShareResult

    data object NotAllowed : ShareResult

    data object AlreadyMember : ShareResult

    data object RoleNotFound : ShareResult
}

sealed interface RevokeResult {
    data object Success : RevokeResult

    data object NotAllowed : RevokeResult

    data object CannotModifySelf : RevokeResult

    data object MemberNotFound : RevokeResult
}

@Service
class ManageSnippetMembersService(
    private val checkPermission: CheckUserPermissionUseCase,
    private val updateUserRole: UpdateUserRoleUseCase,
    private val addMember: AddSnippetMemberUseCase,
    private val removeMember: RemoveSnippetMemberUseCase,
) {
    @Transactional
    fun changeRole( // Cambiar el rol de un usuario el cual ya tiene acceso al snippet
        requesterId: String,
        targetUserId: String,
        snippetId: String,
        roleId: String,
    ): ChangeRoleResult {
        if (!checkPermission(requesterId, snippetId, Permissions.SHARE)) {
            return ChangeRoleResult.NotAllowed
        }
        // Después con auth0, hacer la autenticación de usuarios aca.

        if (requesterId == targetUserId) return ChangeRoleResult.CannotModifySelf

        return when (updateUserRole(targetUserId, snippetId, roleId)) {
            UpdateUserRoleResult.Success -> ChangeRoleResult.Success
            UpdateUserRoleResult.MemberNotFound -> ChangeRoleResult.MemberNotFound
            UpdateUserRoleResult.RoleNotFound -> ChangeRoleResult.RoleNotFound
        }
    }

    @Transactional
    fun share( // agregar un usuario nuevo con acceso al snippet
        requesterId: String,
        targetUserId: String,
        snippetId: String,
        roleId: String,
    ): ShareResult {
        if (!checkPermission(requesterId, snippetId, Permissions.SHARE)) {
            return ShareResult.NotAllowed
        }
        // Después con auth0, hacer la autenticación de usuarios aca.

        return when (addMember(targetUserId, snippetId, roleId)) {
            CreateUserPermissionResult.Success -> ShareResult.Success
            CreateUserPermissionResult.AlreadyMember -> ShareResult.AlreadyMember
            CreateUserPermissionResult.RoleNotFound -> ShareResult.RoleNotFound
        }
    }

    @Transactional
    fun revoke( // borrar un usuario de un snippet
        requesterId: String,
        targetUserId: String,
        snippetId: String,
    ): RevokeResult {
        if (!checkPermission(requesterId, snippetId, Permissions.SHARE)) {
            return RevokeResult.NotAllowed
        }
        // Después con auth0, hacer la autenticación de usuarios aca.

        if (requesterId == targetUserId) return RevokeResult.CannotModifySelf

        return when (removeMember(targetUserId, snippetId)) {
            DeleteUserPermissionResult.Success -> RevokeResult.Success
            DeleteUserPermissionResult.MemberNotFound -> RevokeResult.MemberNotFound
        }
    }
}
