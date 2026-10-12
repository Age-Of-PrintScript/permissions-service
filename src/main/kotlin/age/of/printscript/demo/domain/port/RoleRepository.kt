package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.Role

interface RoleRepository {
    fun findById(id: Long): Role?
}
