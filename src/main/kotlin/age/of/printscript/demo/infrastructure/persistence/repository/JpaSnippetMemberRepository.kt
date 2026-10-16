package age.of.printscript.demo.infrastructure.persistence.repository

import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaSnippetMemberRepository : JpaRepository<SnippetMemberEntity, String> {
    fun findBySnippetId(snippetId: String): List<SnippetMemberEntity>

    fun existsByUserIdAndSnippetIdAndRoleId(
        userId: String,
        snippetId: String,
        roleId: String,
    ): Boolean

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

    @Query(
        """
        select distinct p
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
}
