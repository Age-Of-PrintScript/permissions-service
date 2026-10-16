package application

import age.of.printscript.demo.application.CheckUserPermissionUseCase
import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import support.FakeSnippetMemberRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckUserPermissionUseCaseTest {
    private val owner = Role("1", "OWNER")
    private val viewer = Role("2", "VIEWER")

    private fun repo() =
        FakeSnippetMemberRepository().apply {
            givenRolePermissions(owner, listOf(Permission("1", "READ"), Permission("2", "WRITE")))
            givenRolePermissions(viewer, listOf(Permission("1", "READ")))
            save(SnippetMember(userId = "ana", snippetId = "10", role = owner))
            save(SnippetMember(userId = "beto", snippetId = "11", role = viewer))
        }

    @Test
    fun `is true when the role grants the permission`() {
        val check = CheckUserPermissionUseCase(repo())
        assertTrue(check("ana", "10", "READ"))
        assertTrue(check("beto", "11", "READ"))
    }

    @Test
    fun `is false when the role does not grant the permission`() {
        assertFalse(CheckUserPermissionUseCase(repo())("beto", "10", "WRITE"))
    }

    @Test
    fun `is false for a permission that does not exist`() {
        assertFalse(CheckUserPermissionUseCase(repo())("ana", "10", "DELETE_EVERYTHING"))
    }

    @Test
    fun `is false when the user is not a member of the snippet`() {
        val check = CheckUserPermissionUseCase(repo())
        assertFalse(check("ana", "99", "READ"))
        assertFalse(check("nadie", "10", "READ"))
    }
}
