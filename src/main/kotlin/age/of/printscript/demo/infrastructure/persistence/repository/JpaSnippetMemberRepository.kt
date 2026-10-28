package age.of.printscript.demo.infrastructure.persistence.repository

import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaSnippetMemberRepository : JpaRepository<SnippetMemberEntity, String> {
    fun findBySnippetId(snippetId: String): List<SnippetMemberEntity>

    fun existsByUserIdAndSnippetId(
        userId: String,
        snippetId: String,
    ): Boolean

    fun deleteByUserIdAndSnippetId(
        userId: String,
        snippetId: String,
    ): Int

    @Query(
        """
        select count(m) > 0
        from SnippetMemberEntity m
        where m.userId = :userId
          and m.snippetId = :snippetId
          and m.role.id = :roleId
        """,
    )
    fun existsMemberWithRole(
        @Param("userId") userId: String,
        @Param("snippetId") snippetId: String,
        @Param("roleId") roleId: String,
    ): Boolean

    @Modifying(clearAutomatically = true)
    @Query(
        """
        update SnippetMemberEntity m
        set m.role = :role
        where m.userId = :userId
          and m.snippetId = :snippetId
        """,
    )
    fun updateUserRole(
        @Param("userId") userId: String,
        @Param("snippetId") snippetId: String,
        @Param("role") role: RoleEntity,
    ): Int

    @Query(
        """
        select p
        from SnippetMemberEntity m
        join m.role r
        join r.permissions p
        where m.userId = :userId
          and m.snippetId = :snippetId
        """,
    )
    fun findPermissions(
        @Param("userId") userId: String,
        @Param("snippetId") snippetId: String,
    ): List<PermissionEntity>

    @Query(
        """
        select count(m) > 0
        from SnippetMemberEntity m
        join m.role r
        join r.permissions p
        where m.userId = :userId
          and m.snippetId = :snippetId
          and p.name = :permission
        """,
    )
    fun hasPermission(
        @Param("userId") userId: String,
        @Param("snippetId") snippetId: String,
        @Param("permission") permission: String,
    ): Boolean
}
