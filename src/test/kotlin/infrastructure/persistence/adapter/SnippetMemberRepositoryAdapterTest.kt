package infrastructure.persistence.adapter

import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.domain.port.AddMemberOutcome
import age.of.printscript.demo.domain.port.RemoveMemberOutcome
import age.of.printscript.demo.domain.port.UpdateMemberRoleOutcome
import age.of.printscript.demo.infrastructure.persistence.adapter.SnippetMemberRepositoryAdapter
import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import age.of.printscript.demo.infrastructure.persistence.repository.JpaSnippetMemberRepository
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SnippetMemberRepositoryAdapterTest {
    private val jpa = mock(JpaSnippetMemberRepository::class.java)
    private val roleJpa = mock(JpaRoleRepository::class.java)
    private val adapter = SnippetMemberRepositoryAdapter(jpa, roleJpa)

    private val roleEntity = RoleEntity(id = "1", name = "OWNER")
    private val memberEntity = SnippetMemberEntity(id = "7", userId = "ana", snippetId = "10", role = roleEntity)

    @Test
    fun `findById maps entity to domain when present`() {
        `when`(jpa.findById("7")).thenReturn(Optional.of(memberEntity))
        assertEquals(SnippetMember("7", "ana", "10", SnippetRole("1", "OWNER")), adapter.findById("7"))
    }

    @Test
    fun `findById returns null when entity not found`() {
        `when`(jpa.findById("99")).thenReturn(Optional.empty())
        assertNull(adapter.findById("99"))
    }

    @Test
    fun `findBySnippetId maps entities to domain`() {
        `when`(jpa.findBySnippetId("10")).thenReturn(listOf(memberEntity))
        val result = adapter.findBySnippetId("10")
        assertEquals(listOf(SnippetMember("7", "ana", "10", SnippetRole("1", "OWNER"))), result)
    }

    @Test
    fun `updateRole returns Updated when row is updated`() {
        `when`(roleJpa.getReferenceById("1")).thenReturn(roleEntity)
        `when`(jpa.updateUserRole("ana", "10", roleEntity)).thenReturn(1)

        val result = adapter.updateRole("ana", "10", "1")
        assertEquals(UpdateMemberRoleOutcome.Updated, result)
    }

    @Test
    fun `updateRole returns MemberNotFound when no row updated`() {
        `when`(roleJpa.getReferenceById("1")).thenReturn(roleEntity)
        `when`(jpa.updateUserRole("ana", "10", roleEntity)).thenReturn(0)

        val result = adapter.updateRole("ana", "10", "1")
        assertEquals(UpdateMemberRoleOutcome.MemberNotFound, result)
    }

    @Test
    fun `hasPermission and existsMemberWithRole delegate to jpa`() {
        `when`(jpa.hasPermission("ana", "10", "READ")).thenReturn(true)
        assertTrue(adapter.hasPermission("ana", "10", "READ"))
    }

    @Test
    fun `findPermissions maps permission entities`() {
        `when`(jpa.findPermissions("ana", "10")).thenReturn(listOf(PermissionEntity(id = "1", name = "READ")))
        assertEquals("READ", adapter.findPermissions("ana", "10").single().name)
    }

    @Test
    fun `add returns AlreadyMember when user already exists for snippet`() {
        `when`(jpa.existsByUserIdAndSnippetId("ana", "10")).thenReturn(true)

        val result = adapter.add("ana", "10", "1")
        assertEquals(AddMemberOutcome.AlreadyMember, result)
    }

    @Test
    fun `add returns Added when user is newly added`() {
        `when`(jpa.existsByUserIdAndSnippetId("ana", "10")).thenReturn(false)
        `when`(roleJpa.getReferenceById("1")).thenReturn(roleEntity)

        val result = adapter.add("ana", "10", "1")
        assertEquals(AddMemberOutcome.Added, result)
    }

    @Test
    fun `delete returns Removed when row is deleted`() {
        `when`(jpa.deleteByUserIdAndSnippetId("ana", "10")).thenReturn(1)

        val result = adapter.delete("ana", "10")
        assertEquals(RemoveMemberOutcome.Removed, result)
    }

    @Test
    fun `delete returns MemberNotFound when no row deleted`() {
        `when`(jpa.deleteByUserIdAndSnippetId("ana", "10")).thenReturn(0)

        val result = adapter.delete("ana", "10")
        assertEquals(RemoveMemberOutcome.MemberNotFound, result)
    }
}
