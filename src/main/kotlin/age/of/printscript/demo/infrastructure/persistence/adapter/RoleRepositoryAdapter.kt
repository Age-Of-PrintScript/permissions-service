package age.of.printscript.demo.infrastructure.persistence.adapter

import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.domain.port.RoleRepository
import age.of.printscript.demo.infrastructure.persistence.mapper.toDomain
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.stereotype.Repository

@Repository
class RoleRepositoryAdapter(
    private val jpa: JpaRoleRepository,
) : RoleRepository {
    override fun findById(id: String): SnippetRole? = jpa.findById(id).orElse(null)?.toDomain()

    override fun existsById(roleId: String): Boolean = jpa.existsById(roleId)
}
