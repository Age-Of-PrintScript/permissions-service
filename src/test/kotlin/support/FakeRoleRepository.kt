package support

import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.domain.port.RoleRepository

class FakeRoleRepository(
    private val roleIds: Set<String>,
) : RoleRepository {
    override fun findById(id: String): SnippetRole? = if (id in roleIds) SnippetRole(id = id, name = id) else null

    override fun existsById(roleId: String): Boolean = roleId in roleIds
}
