package age.of.printscript.infrastructure.db.repository

import age.of.printscript.domain.PermissionType
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<PermissionType, Long> { // TODO(Engancharle el famoso either de ps para manejo de errores)
    fun findByUserIdAndSnippet(
        userId: Long,
        snippet: String,
    ): List<PermissionType>

    fun existsByUserIdAndSnippetAndPermission(
        userId: Long,
        snippet: String,
        permissionType: PermissionType,
    ): Boolean
}
