package application

import age.of.printscript.demo.application.ChangeRoleResult
import age.of.printscript.demo.application.ManageSnippetMembersService
import age.of.printscript.demo.application.RevokeResult
import age.of.printscript.demo.application.ShareResult
import age.of.printscript.demo.application.command.AddSnippetMemberUseCase
import age.of.printscript.demo.application.command.RemoveSnippetMemberUseCase
import age.of.printscript.demo.application.command.UpdateUserRoleUseCase
import age.of.printscript.demo.application.query.CheckUserPermissionUseCase
import age.of.printscript.demo.domain.Permissions
import support.Fixtures.ALICE
import support.Fixtures.BOB
import support.Fixtures.CAROL
import support.Fixtures.DAVE
import support.Fixtures.EDITOR
import support.Fixtures.OWNER
import support.Fixtures.SNIPPET
import support.Fixtures.VIEWER
import support.Fixtures.memberRepo
import support.Fixtures.roleRepo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ManageSnippetMembersServiceTest {
    private val members = memberRepo(listOf(ALICE to OWNER, BOB to EDITOR, CAROL to VIEWER))
    private val roles = roleRepo()
    private val service =
        ManageSnippetMembersService(
            checkPermission = CheckUserPermissionUseCase(members),
            updateUserRole = UpdateUserRoleUseCase(members, roles),
            addMember = AddSnippetMemberUseCase(members, roles),
            removeMember = RemoveSnippetMemberUseCase(members),
        )

// mismos imports de Fixtures, Permissions y Assertions que arriba

    // ---------- share ----------

    @Test
    fun `share - el owner comparte con un usuario nuevo`() {
        assertEquals(ShareResult.Success, service.share(ALICE, DAVE, SNIPPET, VIEWER))
        assertTrue(members.hasPermission(DAVE, SNIPPET, Permissions.READ))
    }

    @Test
    fun `share - un editor no puede compartir`() {
        assertEquals(ShareResult.NotAllowed, service.share(BOB, DAVE, SNIPPET, VIEWER))
        assertFalse(members.hasPermission(DAVE, SNIPPET, Permissions.READ))
    }

    @Test
    fun `share - quien no es miembro no puede compartir`() {
        assertEquals(ShareResult.NotAllowed, service.share(DAVE, CAROL, SNIPPET, VIEWER))
    }

    @Test
    fun `share - AlreadyMember si el destinatario ya tiene acceso`() {
        assertEquals(ShareResult.AlreadyMember, service.share(ALICE, BOB, SNIPPET, VIEWER))
    }

    @Test
    fun `share - RoleNotFound si el rol no existe`() {
        assertEquals(ShareResult.RoleNotFound, service.share(ALICE, DAVE, SNIPPET, "inexistente"))
    }

    // ---------- changeRole ----------

    @Test
    fun `changeRole - el owner cambia el rol de un miembro`() {
        assertEquals(ChangeRoleResult.Success, service.changeRole(ALICE, BOB, SNIPPET, VIEWER))
        assertFalse(members.hasPermission(BOB, SNIPPET, Permissions.WRITE))
    }

    @Test
    fun `changeRole - un viewer no puede cambiar roles`() {
        assertEquals(ChangeRoleResult.NotAllowed, service.changeRole(CAROL, BOB, SNIPPET, VIEWER))
        assertTrue(members.hasPermission(BOB, SNIPPET, Permissions.WRITE))
    }

    @Test
    fun `changeRole - no se puede modificar el propio rol`() {
        assertEquals(ChangeRoleResult.CannotModifySelf, service.changeRole(ALICE, ALICE, SNIPPET, VIEWER))
        assertTrue(members.hasPermission(ALICE, SNIPPET, Permissions.SHARE))
    }

    @Test
    fun `changeRole - MemberNotFound si el destinatario no es miembro`() {
        assertEquals(ChangeRoleResult.MemberNotFound, service.changeRole(ALICE, DAVE, SNIPPET, VIEWER))
    }

    @Test
    fun `changeRole - RoleNotFound si el rol no existe`() {
        assertEquals(ChangeRoleResult.RoleNotFound, service.changeRole(ALICE, BOB, SNIPPET, "inexistente"))
    }

    // ---------- revoke ----------

    @Test
    fun `revoke - el owner le quita el acceso a un miembro`() {
        assertEquals(RevokeResult.Success, service.revoke(ALICE, BOB, SNIPPET))
        assertFalse(members.hasPermission(BOB, SNIPPET, Permissions.READ))
    }

    @Test
    fun `revoke - un editor no puede revocar`() {
        assertEquals(RevokeResult.NotAllowed, service.revoke(BOB, CAROL, SNIPPET))
        assertTrue(members.hasPermission(CAROL, SNIPPET, Permissions.READ))
    }

    @Test
    fun `revoke - no se puede revocar a uno mismo`() {
        assertEquals(RevokeResult.CannotModifySelf, service.revoke(ALICE, ALICE, SNIPPET))
    }

    @Test
    fun `revoke - MemberNotFound si el destinatario no es miembro`() {
        assertEquals(RevokeResult.MemberNotFound, service.revoke(ALICE, DAVE, SNIPPET))
    }
}
