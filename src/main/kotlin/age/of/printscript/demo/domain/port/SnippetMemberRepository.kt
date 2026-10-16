package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember

interface SnippetMemberRepository {
    fun findById(id: String): SnippetMember?

    fun findBySnippetId(snippetId: String): List<SnippetMember>

    fun existsMemberWithRole(
        userId: String,
        snippetId: String,
        roleId: String,
    ): Boolean

    fun findPermissions(
        userId: String,
        snippetId: String,
    ): List<Permission>

    fun hasPermission(
        userId: String,
        snippetId: String,
        permission: String,
    ): Boolean

    fun save(member: SnippetMember): SnippetMember

    fun delete(id: String)
}
