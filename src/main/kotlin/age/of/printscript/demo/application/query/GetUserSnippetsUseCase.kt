package age.of.printscript.demo.application.query

import age.of.printscript.demo.domain.Permissions
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import org.springframework.stereotype.Service

@Service
class GetUserSnippetsUseCase(
    private val members: SnippetMemberRepository,
) {
    operator fun invoke(userId: String): List<SnippetMember> = members.findByUserIdAndPermission(userId, Permissions.READ)
}
