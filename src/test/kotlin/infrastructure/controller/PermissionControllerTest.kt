package infrastructure.controller

import age.of.printscript.demo.application.GetUserPermissionsUseCase
import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.infrastructure.controller.PermissionController
import age.of.printscript.demo.infrastructure.controller.dto.PermissionResponse
import support.FakeSnippetMemberRepository
import kotlin.collections.emptyList
import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionControllerTest {
    @Test
    fun `list maps domain permissions to responses`() {
        val role = Role("1", "OWNER")
        val repo =
            FakeSnippetMemberRepository().apply {
                givenRolePermissions(role, listOf(Permission("1", "READ"), Permission("2", "WRITE")))
                save(SnippetMember(userId = "ana", snippetId = "10", role = role))
            }
        val controller = PermissionController(GetUserPermissionsUseCase(repo))

        val result = controller.list("ana", "10")

        assertEquals(listOf(PermissionResponse("1", "READ"), PermissionResponse("2", "WRITE")), result)
    }

    @Test
    fun `list returns empty when there are no permissions`() {
        val controller = PermissionController(GetUserPermissionsUseCase(FakeSnippetMemberRepository()))
        assertEquals(emptyList(), controller.list("x", "1"))
    }
}
