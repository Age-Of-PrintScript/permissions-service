package age.of.printscript.demo.infrastructure.persistence.repository

import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaRoleRepository : JpaRepository<RoleEntity, String>
