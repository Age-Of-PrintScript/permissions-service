package age.of.printscript.demo.application.command

import age.of.printscript.demo.domain.RoleNames
import age.of.printscript.demo.domain.port.AddMemberOutcome
import age.of.printscript.demo.domain.port.RoleRepository
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

sealed interface RegisterOwnerResult {
    data object Success : RegisterOwnerResult

    data object AlreadyHasOwner : RegisterOwnerResult

    data object OwnerRoleMissing : RegisterOwnerResult
}

/**
 * Registra al dueño de un snippet recién creado. Solo es válido mientras el snippet no tenga
 * miembros: evita que un usuario se adueñe de un snippet ajeno.
 */
@Service
class RegisterSnippetOwnerUseCase(
    private val members: SnippetMemberRepository,
    private val roles: RoleRepository,
) {
    operator fun invoke(
        ownerId: String,
        snippetId: String,
    ): RegisterOwnerResult {
        val ownerRole = roles.findByName(RoleNames.OWNER) ?: return RegisterOwnerResult.OwnerRoleMissing

        if (members.findBySnippetId(snippetId).isNotEmpty()) return RegisterOwnerResult.AlreadyHasOwner

        return when (members.add(ownerId, snippetId, ownerRole.id)) {
            AddMemberOutcome.Added -> RegisterOwnerResult.Success
            AddMemberOutcome.AlreadyMember -> RegisterOwnerResult.AlreadyHasOwner
        }
    }
}
