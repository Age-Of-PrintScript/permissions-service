package infrastructure.persistence.adapter

import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.infrastructure.persistence.adapter.SnippetMemberRepositoryAdapter
import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.entity.UserSnippetRoleEntity
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import age.of.printscript.demo.infrastructure.persistence.repository.JpaSnippetMemberRepository
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnippetMemberRepositoryAdapterTest {
    private val jpa = mock(JpaSnippetMemberRepository::class.java)
    private val roleJpa = mock(JpaRoleRepository::class.java)
    private val adapter = SnippetMemberRepositoryAdapter(jpa, roleJpa)

    private val roleEntity = UserSnippetRoleEntity(id = 1, name = "OWNER")
    private val memberEntity = SnippetMemberEntity(id = 7, userId = "ana", snippetId = 10, role = roleEntity)

    @Test
    fun `findBySnippetId maps entities to domain`() {
        `when`(jpa.findBySnippetId(10)).thenReturn(listOf(memberEntity))
        val result = adapter.findBySnippetId(10)
        assertEquals(listOf(SnippetMember(7, "ana", 10, Role(1, "OWNER"))), result)
    }

    @Test
    fun `hasPermission and existsMemberWithRole delegate to jpa`() {
        `when`(jpa.hasPermission("ana", 10, "READ")).thenReturn(true)
        `when`(jpa.existsByUserIdAndSnippetIdAndRoleId("ana", 10, 1)).thenReturn(true)
        assertTrue(adapter.hasPermission("ana", 10, "READ"))
        assertTrue(adapter.existsMemberWithRole("ana", 10, 1))
    }

    @Test
    fun `findPermissions maps permission entities`() {
        `when`(jpa.findPermissions("ana", 10)).thenReturn(listOf(PermissionEntity(id = 1, name = "READ")))
        assertEquals("READ", adapter.findPermissions("ana", 10).single().name)
    }

    @Test
    fun `save loads the role and returns the saved member`() {
        `when`(roleJpa.findById(1L)).thenReturn(Optional.of(roleEntity))
        `when`(jpa.save(Mockito.any(SnippetMemberEntity::class.java))).thenReturn(memberEntity)

        val saved = adapter.save(SnippetMember(userId = "ana", snippetId = 10, role = Role(1, "OWNER")))

        assertEquals(7L, saved.id)
    }
}
