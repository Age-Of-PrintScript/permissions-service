package age.of.printscript.demo.application

import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

@Service
class CheckUserPermissionUseCase(
    private val members: SnippetMemberRepository,
) {
    operator fun invoke(
        userId: String,
        snippetId: Long,
        permission: String,
    ): Boolean = members.hasPermission(userId, snippetId, permission)
}
