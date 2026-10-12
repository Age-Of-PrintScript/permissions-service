package age.of.printscript.demo.infrastructure.persistence.repository

import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaSnippetMemberRepository : JpaRepository<SnippetMemberEntity, Long> {
    fun findBySnippetId(snippetId: Long): List<SnippetMemberEntity>

    fun existsByUserIdAndSnippetIdAndRoleId(
        userId: String,
        snippetId: Long,
        roleId: Long,
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
        @Param("snippetId") snippetId: Long,
        @Param("permission") permission: String,
    ): Boolean
}
