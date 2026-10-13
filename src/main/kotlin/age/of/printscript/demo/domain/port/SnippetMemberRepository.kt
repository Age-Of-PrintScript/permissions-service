package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember

interface SnippetMemberRepository {
    fun findById(id: Long): SnippetMember?

    fun findBySnippetId(snippetId: Long): List<SnippetMember>

    fun existsMemberWithRole(
        userId: String,
        snippetId: Long,
        roleId: Long,
    ): Boolean

    fun findPermissions(
        userId: String,
        snippetId: Long,
    ): List<Permission>

    fun hasPermission(
        userId: String,
        snippetId: Long,
        permission: String,
    ): Boolean

    fun save(member: SnippetMember): SnippetMember

    fun delete(id: Long)
}
