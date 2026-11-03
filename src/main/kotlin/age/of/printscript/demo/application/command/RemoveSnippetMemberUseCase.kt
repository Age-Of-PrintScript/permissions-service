package age.of.printscript.demo.application.command

import age.of.printscript.demo.domain.port.RemoveMemberOutcome
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

sealed interface DeleteUserPermissionResult {
    data object Success : DeleteUserPermissionResult

    data object MemberNotFound : DeleteUserPermissionResult
}

@Service
class RemoveSnippetMemberUseCase(
    private val members: SnippetMemberRepository,
) {
    operator fun invoke(
        targetUserId: String,
        snippetId: String,
    ): DeleteUserPermissionResult =
        when (members.delete(targetUserId, snippetId)) {
            RemoveMemberOutcome.Removed -> DeleteUserPermissionResult.Success
            RemoveMemberOutcome.MemberNotFound -> DeleteUserPermissionResult.MemberNotFound
        }
}
