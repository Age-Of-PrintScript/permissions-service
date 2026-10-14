package age.of.printscript.demo.application

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

@Service
class GetUserPermissionsUseCase(
    private val members: SnippetMemberRepository,
) {
    operator fun invoke(
        userId: String,
        snippetId: String,
    ): List<Permission> = members.findPermissions(userId, snippetId)
}
