package age.of.printscript.demo.infrastructure.persistence.adapter

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.port.AddMemberOutcome
import age.of.printscript.demo.domain.port.RemoveMemberOutcome
import age.of.printscript.demo.domain.port.SnippetMemberRepository
import age.of.printscript.demo.domain.port.UpdateMemberRoleOutcome
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.mapper.toDomain
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import age.of.printscript.demo.infrastructure.persistence.repository.JpaSnippetMemberRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class SnippetMemberRepositoryAdapter(
    private val jpa: JpaSnippetMemberRepository,
    private val roleJpa: JpaRoleRepository,
) : SnippetMemberRepository {
    override fun findById(id: String): SnippetMember? = jpa.findById(id).orElse(null)?.toDomain()

    override fun findBySnippetId(snippetId: String): List<SnippetMember> = jpa.findBySnippetId(snippetId).map { it.toDomain() }

    @Transactional
    override fun updateRole(
        userId: String,
        snippetId: String,
        roleId: String,
    ): UpdateMemberRoleOutcome {
        val role = roleJpa.getReferenceById(roleId)
        val updatedRows = jpa.updateUserRole(userId, snippetId, role)
        return if (updatedRows > 0) UpdateMemberRoleOutcome.Updated else UpdateMemberRoleOutcome.MemberNotFound
    }

    override fun hasPermission(
        userId: String,
        snippetId: String,
        permission: String,
    ): Boolean = jpa.hasPermission(userId, snippetId, permission)

    override fun findPermissions(
        userId: String,
        snippetId: String,
    ): List<Permission> = jpa.findPermissions(userId, snippetId).map { it.toDomain() }

    @Transactional
    override fun add(
        userId: String,
        snippetId: String,
        roleId: String,
    ): AddMemberOutcome {
        if (jpa.existsByUserIdAndSnippetId(userId, snippetId)) return AddMemberOutcome.AlreadyMember

        jpa.save(SnippetMemberEntity(userId = userId, snippetId = snippetId, role = roleJpa.getReferenceById(roleId)))
        return AddMemberOutcome.Added
    }

    @Transactional
    override fun delete(
        userId: String,
        snippetId: String,
    ): RemoveMemberOutcome =
        if (jpa.deleteByUserIdAndSnippetId(userId, snippetId) > 0) {
            RemoveMemberOutcome.Removed
        } else {
            RemoveMemberOutcome.MemberNotFound
        }
}
