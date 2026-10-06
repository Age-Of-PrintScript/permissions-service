package age.of.printscript.infrastructure.db.repository

import age.of.printscript.domain.RoleType
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<RoleType, Long> { // TODO(Engancharle el famoso either de ps para manejo de errores)
    fun findByUserId(userId: String): List<RoleType>
}
