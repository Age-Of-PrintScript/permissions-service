package age.of.printscript.demo.infrastructure.persistence.repository

import age.of.printscript.demo.infrastructure.persistence.entity.UserSnippetRoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaRoleRepository : JpaRepository<UserSnippetRoleEntity, String>
