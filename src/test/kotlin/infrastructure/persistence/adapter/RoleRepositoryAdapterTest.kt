package infrastructure.persistence.adapter

import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.infrastructure.persistence.adapter.RoleRepositoryAdapter
import age.of.printscript.demo.infrastructure.persistence.entity.UserSnippetRoleEntity
import age.of.printscript.demo.infrastructure.persistence.repository.JpaRoleRepository
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RoleRepositoryAdapterTest {
    private val jpa = mock(JpaRoleRepository::class.java)
    private val adapter = RoleRepositoryAdapter(jpa)

    @Test
    fun `findById maps the entity to domain`() {
        `when`(jpa.findById("1")).thenReturn(Optional.of(UserSnippetRoleEntity(id = "1", name = "OWNER")))

        assertEquals(Role("1", "OWNER"), adapter.findById("1"))
    }

    @Test
    fun `findById returns null when the role does not exist`() {
        `when`(jpa.findById("4")).thenReturn(Optional.empty())

        assertNull(adapter.findById("4"))
    }
}
