package support

import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.port.RoleRepository

class FakeRoleRepository : RoleRepository {
    private val roles = mutableMapOf<String, Role>()

    fun givenRole(role: Role) {
        roles[role.id] = role
    }

    override fun findById(id: String): Role? = roles[id]
}
