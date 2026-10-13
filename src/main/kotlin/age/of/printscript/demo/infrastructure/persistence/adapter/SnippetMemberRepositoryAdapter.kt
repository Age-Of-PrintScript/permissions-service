package age.of.printscript.demo.infrastructure.persistence.adapter

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.mapper.toDomain
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import age.of.printscript.demo.infrastructure.persistence.repository.JpaSnippetMemberRepository
import org.springframework.stereotype.Repository

@Repository
class SnippetMemberRepositoryAdapter(
    private val jpa: JpaSnippetMemberRepository,
    private val roleJpa: JpaRoleRepository,
) : SnippetMemberRepository {
    override fun findById(id: Long): SnippetMember? = jpa.findById(id).orElse(null)?.toDomain()

    override fun findBySnippetId(snippetId: Long): List<SnippetMember> = jpa.findBySnippetId(snippetId).map { it.toDomain() }

    override fun existsMemberWithRole(
        userId: String,
        snippetId: Long,
        roleId: Long,
    ): Boolean = jpa.existsByUserIdAndSnippetIdAndRoleId(userId, snippetId, roleId)

    override fun hasPermission(
        userId: String,
        snippetId: Long,
        permission: String,
    ): Boolean = jpa.hasPermission(userId, snippetId, permission)

    override fun findPermissions(
        userId: String,
        snippetId: Long,
    ): List<Permission> = jpa.findPermissions(userId, snippetId).map { it.toDomain() }

    override fun save(member: SnippetMember): SnippetMember {
        val roleEntity = roleJpa.findById(member.role.id).orElseThrow()

        return jpa
            .save(
                SnippetMemberEntity(
                    userId = member.userId,
                    snippetId = member.snippetId,
                    role = roleEntity,
                ),
            ).toDomain()
    }

    override fun delete(id: Long) = jpa.deleteById(id)
}
