package infrastructure.persistence.entity

import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EntitiesTest {
    @Test
    fun `entities hold their values`() {
        val perm = PermissionEntity(id = "1", name = "READ")
        val role = RoleEntity(id = "1", name = "OWNER", permissions = mutableSetOf(perm))
        val member = SnippetMemberEntity(id = "1", userId = "ana", snippetId = "10", role = role)

        assertEquals("READ", perm.name)
        assertTrue(role.permissions.contains(perm))
        assertEquals("OWNER", member.role.name)
    }

    @Test
    fun `entities default values`() {
        val perm = PermissionEntity(name = "WRITE")
        val role = RoleEntity(name = "VIEWER")
        val member = SnippetMemberEntity(userId = "bob", snippetId = "20", role = role)

        assertNull(perm.id)
        assertNull(role.id)
        assertTrue(role.permissions.isEmpty())
        assertNull(member.id)
        assertEquals("bob", member.userId)
        assertEquals("20", member.snippetId)
    }
}
