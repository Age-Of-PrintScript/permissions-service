package infrastructure.persistence.adapter

import age.of.printscript.demo.domain.Role
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import support.FakeRoleRepository
import kotlin.test.assertEquals

class RoleRepositoryAdapterTest {
    val roles =
        FakeRoleRepository(
            mutableListOf(
                Role("1", "Owner"),
                Role("2", "Developer"),
                Role("3", "Administrator"),
            ),
        )

    @Test
    fun `should load role by id`() {
        assertEquals(("Owner"), roles.findById("1")!!.name)
    }

    @Test
    fun `should return null if not found`() {
        assertNull(roles.findById("4")?.name)
    }
}
