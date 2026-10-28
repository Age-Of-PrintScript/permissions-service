package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.SnippetRole

interface RoleRepository {
    fun findById(id: String): SnippetRole?

    fun existsById(roleId: String): Boolean
}
