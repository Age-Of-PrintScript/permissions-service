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
        roleName: String,
    ): CreateUserPermissionResult {
        val normalized = roleName.trim().uppercase()
        // OWNER solo se asigna al crear el snippet (RegisterSnippetOwnerUseCase), nunca al compartir.
        if (normalized == RoleNames.OWNER) return CreateUserPermissionResult.OwnerRoleNotAssignable

        val role = roles.findByName(normalized) ?: return CreateUserPermissionResult.RoleNotFound

        return when (members.add(targetUserId, snippetId, role.id)) {
            AddMemberOutcome.Added -> CreateUserPermissionResult.Success
            AddMemberOutcome.AlreadyMember -> CreateUserPermissionResult.AlreadyMember
        }
    }
}
