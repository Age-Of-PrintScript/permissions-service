import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.mapper.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MappersTest {
    @Test
    fun `maps a permission entity to the domain permission`() {
        val entity = PermissionEntity(id = "1", name = "snippet:read")

        assertEquals(Permission(id = "1", name = "snippet:read"), entity.toDomain())
    }

    @Test
    fun `maps a role entity to the domain role`() {
        val entity = RoleEntity(id = "2", name = "EDITOR")

        assertEquals(SnippetRole(id = "2", name = "EDITOR"), entity.toDomain())
    }

    @Test
    fun `maps a snippet member including its nested role`() {
        val entity =
            SnippetMemberEntity(
                id = "10",
                userId = "123",
                snippetId = "124",
                role = RoleEntity(id = "2", name = "EDITOR"),
            )

        val expected =
            SnippetMember(
                id = "10",
                userId = "123",
                snippetId = "124",
                snippetRole = SnippetRole(id = "2", name = "EDITOR"),
            )

        assertEquals(expected, entity.toDomain())
    }

    @Test
    fun `fails when a role entity has no id`() {
        assertFailsWith<NullPointerException> {
            RoleEntity(id = null, name = "EDITOR").toDomain()
        }
    }

    @Test
    fun `fails when a snippet member entity has no id`() {
        val entity =
            SnippetMemberEntity(
                id = null,
                userId = "123",
                snippetId = "124",
                role = RoleEntity(id = "2", name = "EDITOR"),
            )

        assertFailsWith<NullPointerException> { entity.toDomain() }
    }
}
