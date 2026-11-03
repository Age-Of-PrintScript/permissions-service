package application

import age.of.printscript.demo.application.command.AddSnippetMemberUseCase
import age.of.printscript.demo.application.command.CreateUserPermissionResult
import age.of.printscript.demo.application.command.DeleteUserPermissionResult
import age.of.printscript.demo.application.command.RemoveSnippetMemberUseCase
import age.of.printscript.demo.application.command.UpdateUserRoleResult
import age.of.printscript.demo.application.command.UpdateUserRoleUseCase
import age.of.printscript.demo.domain.Permissions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import support.Fixtures.ALICE
import support.Fixtures.BOB
import support.Fixtures.DAVE
import support.Fixtures.EDITOR
import support.Fixtures.OWNER
import support.Fixtures.SNIPPET
import support.Fixtures.VIEWER
import support.Fixtures.memberRepo
import support.Fixtures.roleRepo

class UpdateUserRoleUseCaseTest {
    private val members = memberRepo(listOf(BOB to EDITOR))
    private val update = UpdateUserRoleUseCase(members, roleRepo())

    @Test
    fun `cambia el rol de un miembro existente`() {
        assertEquals(UpdateUserRoleResult.Success, update(BOB, SNIPPET, VIEWER))
        assertFalse(members.hasPermission(BOB, SNIPPET, Permissions.WRITE))
    }

    @Test
    fun `devuelve MemberNotFound si el usuario no es miembro`() {
        assertEquals(UpdateUserRoleResult.MemberNotFound, update(DAVE, SNIPPET, VIEWER))
    }

    @Test
    fun `devuelve RoleNotFound si el rol no existe y no modifica nada`() {
        assertEquals(UpdateUserRoleResult.RoleNotFound, update(BOB, SNIPPET, "inexistente"))
        assertTrue(members.hasPermission(BOB, SNIPPET, Permissions.WRITE))
    }
}

class AddSnippetMemberUseCaseTest {
    private val members = memberRepo(listOf(ALICE to OWNER))
    private val add = AddSnippetMemberUseCase(members, roleRepo())

    @Test
    fun `agrega un miembro nuevo con el rol indicado`() {
        assertEquals(CreateUserPermissionResult.Success, add(DAVE, SNIPPET, VIEWER))
        assertTrue(members.hasPermission(DAVE, SNIPPET, Permissions.READ))
        assertFalse(members.hasPermission(DAVE, SNIPPET, Permissions.WRITE))
    }

    @Test
    fun `devuelve AlreadyMember si el usuario ya tiene acceso`() {
        assertEquals(CreateUserPermissionResult.AlreadyMember, add(ALICE, SNIPPET, VIEWER))
        assertTrue(members.hasPermission(ALICE, SNIPPET, Permissions.SHARE)) // sigue siendo owner
    }

    @Test
    fun `devuelve RoleNotFound si el rol no existe y no agrega al miembro`() {
        assertEquals(CreateUserPermissionResult.RoleNotFound, add(DAVE, SNIPPET, "inexistente"))
        assertFalse(members.hasPermission(DAVE, SNIPPET, Permissions.READ))
    }
}

class RemoveSnippetMemberUseCaseTest {
    private val members = memberRepo(listOf(ALICE to OWNER, BOB to EDITOR))
    private val remove = RemoveSnippetMemberUseCase(members)

    @Test
    fun `elimina a un miembro existente`() {
        assertEquals(DeleteUserPermissionResult.Success, remove(BOB, SNIPPET))
        assertFalse(members.hasPermission(BOB, SNIPPET, Permissions.READ))
    }

    @Test
    fun `devuelve MemberNotFound si el usuario no es miembro`() {
        assertEquals(DeleteUserPermissionResult.MemberNotFound, remove(DAVE, SNIPPET))
    }
}
