package support

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.domain.port.AddMemberOutcome
import age.of.printscript.demo.domain.port.RemoveMemberOutcome
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import age.of.printscript.demo.domain.port.UpdateMemberRoleOutcome

class FakeSnippetMemberRepository : SnippetMemberRepository {
    private val members = mutableListOf<SnippetMember>()
    private val rolePermissions = mutableMapOf<String, List<Permission>>()
    private var nextId = 0

    fun givenRolePermissions(
        snippetRole: SnippetRole,
        permissions: List<Permission>,
    ) {
        rolePermissions[snippetRole.id] = permissions
    }

    override fun findById(id: String) = members.firstOrNull { it.id == id }

    override fun findBySnippetId(snippetId: String) = members.filter { it.snippetId == snippetId }

    override fun updateRole(
        userId: String,
        snippetId: String,
        roleId: String,
    ): UpdateMemberRoleOutcome {
        val index = members.indexOfFirst { it.userId == userId && it.snippetId == snippetId }
        if (index == -1) return UpdateMemberRoleOutcome.MemberNotFound

        members[index] = members[index].copy(snippetRole = SnippetRole(id = roleId, name = "test"))
        return UpdateMemberRoleOutcome.Updated
    }

    override fun findPermissions(
        userId: String,
        snippetId: String,
    ): List<Permission> =
        members
            .filter { it.userId == userId && it.snippetId == snippetId }
            .flatMap { rolePermissions[it.snippetRole.id].orEmpty() }
            .distinctBy { it.id }

    override fun hasPermission(
        userId: String,
        snippetId: String,
        permission: String,
    ) = findPermissions(userId, snippetId).any { it.name == permission }

    override fun add(
        userId: String,
        snippetId: String,
        roleId: String,
    ): AddMemberOutcome {
        if (members.any { it.userId == userId && it.snippetId == snippetId }) {
            return AddMemberOutcome.AlreadyMember
        }

        members +=
            SnippetMember(
                id = "member-${nextId++}",
                snippetId = snippetId,
                userId = userId,
                snippetRole = SnippetRole(id = roleId, name = "test"),
            )
        return AddMemberOutcome.Added
    }

    override fun delete(
        userId: String,
        snippetId: String,
    ): RemoveMemberOutcome =
        if (members.removeIf { it.userId == userId && it.snippetId == snippetId }) {
            RemoveMemberOutcome.Removed
        } else {
            RemoveMemberOutcome.MemberNotFound
        }
}
