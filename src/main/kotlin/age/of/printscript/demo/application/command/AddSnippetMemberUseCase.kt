package age.of.printscript.demo.application.command

import age.of.printscript.demo.domain.RoleNames
import age.of.printscript.demo.domain.port.AddMemberOutcome
import age.of.printscript.demo.domain.port.RoleRepository
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

sealed interface CreateUserPermissionResult {
    data object Success : CreateUserPermissionResult

    data object AlreadyMember : CreateUserPermissionResult

    data object RoleNotFound : CreateUserPermissionResult

    data object OwnerRoleNotAssignable : CreateUserPermissionResult
}

@Service
class AddSnippetMemberUseCase(
    private val members: SnippetMemberRepository,
    private val roles: RoleRepository,
) {
    operator fun invoke(
        targetUserId: String,
        snippetId: String,
        roleId: String,
    ): CreateUserPermissionResult {
        val role = roles.findById(roleId) ?: return CreateUserPermissionResult.RoleNotFound
        // OWNER solo se asigna al crear el snippet (RegisterSnippetOwnerUseCase), nunca al compartir.
        if (role.name.equals(RoleNames.OWNER, ignoreCase = true)) return CreateUserPermissionResult.OwnerRoleNotAssignable

        return when (members.add(targetUserId, snippetId, roleId)) {
            AddMemberOutcome.Added -> CreateUserPermissionResult.Success
            AddMemberOutcome.AlreadyMember -> CreateUserPermissionResult.AlreadyMember
        }
    }
}
