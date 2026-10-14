package support

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.port.SnippetMemberRepository

class FakeSnippetMemberRepository : SnippetMemberRepository {
    private val members = mutableListOf<SnippetMember>()
    private val rolePermissions = mutableMapOf<String, List<Permission>>()
    private var nextId = 0

    fun givenRolePermissions(
        role: Role,
        permissions: List<Permission>,
    ) {
        rolePermissions[role.id] = permissions
    }

    override fun findById(id: String) = members.firstOrNull { it.id == id }

    override fun findBySnippetId(snippetId: String) = members.filter { it.snippetId == snippetId }

    override fun existsMemberWithRole(
        userId: String,
        snippetId: String,
        roleId: String,
    ) = members.any { it.userId == userId && it.snippetId == snippetId && it.role.id == roleId }

    override fun findPermissions(
        userId: String,
        snippetId: String,
    ): List<Permission> =
        members
            .filter { it.userId == userId && it.snippetId == snippetId }
            .flatMap { rolePermissions[it.role.id].orEmpty() }
            .distinctBy { it.id }

    override fun hasPermission(
        userId: String,
        snippetId: String,
        permission: String,
    ) = findPermissions(userId, snippetId).any { it.name.equals(permission) }

    override fun save(member: SnippetMember): SnippetMember {
        val saved = member.copy(id = member.id ?: (nextId++).toString())
        members.removeAll { it.id == saved.id }
        members.add(saved)
        return saved
    }

    override fun delete(id: String) {
        members.removeAll { it.id == id }
    }
}
