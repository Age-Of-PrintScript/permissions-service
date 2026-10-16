package support

import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.port.RoleRepository

class FakeRoleRepository(
    val roles: MutableList<Role>,
) : RoleRepository {
    // Funciona igual que el find by id pero en memoria. Si no lo encuentra, null.
    override fun findById(id: String): Role? = roles.find { it.id == id }
}
