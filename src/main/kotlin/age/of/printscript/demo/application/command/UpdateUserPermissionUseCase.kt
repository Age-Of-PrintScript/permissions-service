package age.of.printscript.demo.application.command

import age.of.printscript.demo.domain.port.RoleRepository
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import age.of.printscript.demo.domain.port.UpdateMemberRoleOutcome
import org.springframework.stereotype.Service

sealed interface UpdateUserRoleResult {
    data object Success : UpdateUserRoleResult

    data object MemberNotFound : UpdateUserRoleResult

    data object RoleNotFound : UpdateUserRoleResult
}

@Service
class UpdateUserRoleUseCase(
    private val members: SnippetMemberRepository,
    private val roles: RoleRepository,
) {
    operator fun invoke(
        targetUserId: String,
        snippetId: String,
        roleId: String,
    ): UpdateUserRoleResult {
        if (!roles.existsById(roleId)) return UpdateUserRoleResult.RoleNotFound

        val updated = members.updateRole(targetUserId, snippetId, roleId)
        return when (updated) {
            is UpdateMemberRoleOutcome.MemberNotFound -> UpdateUserRoleResult.MemberNotFound
            is UpdateMemberRoleOutcome.Updated -> UpdateUserRoleResult.Success
        }
    }
}
