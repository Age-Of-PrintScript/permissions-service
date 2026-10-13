package support

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.port.SnippetMemberRepository

class FakeSnippetMemberRepository : SnippetMemberRepository {
    private val members = mutableListOf<SnippetMember>()
    private val rolePermissions = mutableMapOf<Long, List<Permission>>()
    private var nextId = 1L

    fun givenRolePermissions(
        role: Role,
        permissions: List<Permission>,
    ) {
        rolePermissions[role.id] = permissions
    }

    override fun findById(id: Long) = members.firstOrNull { it.id == id }

    override fun findBySnippetId(snippetId: Long) = members.filter { it.snippetId == snippetId }

    override fun existsMemberWithRole(
        userId: String,
        snippetId: Long,
        roleId: Long,
    ) = members.any { it.userId == userId && it.snippetId == snippetId && it.role.id == roleId }

    override fun findPermissions(
        userId: String,
        snippetId: Long,
    ): List<Permission> =
        members
            .filter { it.userId == userId && it.snippetId == snippetId }
            .flatMap { rolePermissions[it.role.id].orEmpty() }
            .distinctBy { it.id }

    override fun hasPermission(
        userId: String,
        snippetId: Long,
        permission: String,
    ) = findPermissions(userId, snippetId).any { it.name == permission }

    override fun save(member: SnippetMember): SnippetMember {
        val saved = member.copy(id = member.id ?: nextId++)
        members.removeAll { it.id == saved.id }
        members.add(saved)
        return saved
    }

    override fun delete(id: Long) {
        members.removeAll { it.id == id }
    }
}
