package infrastructure.controller

import age.of.printscript.demo.application.ChangeRoleResult
import age.of.printscript.demo.application.ManageSnippetMembersService
import age.of.printscript.demo.application.RevokeResult
import age.of.printscript.demo.application.ShareResult
import age.of.printscript.demo.infrastructure.controller.SnippetMembersController
import age.of.printscript.demo.infrastructure.controller.dto.ChangeRoleRequest
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import age.of.printscript.demo.infrastructure.controller.dto.MemberResponse
import age.of.printscript.demo.infrastructure.controller.dto.ShareSnippetRequest
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SnippetMembersControllerTest {
    private val service = mock(ManageSnippetMembersService::class.java)
    private val controller = SnippetMembersController(service)

    // --- share ---

    @Test
    fun `share returns 201 CREATED on success`() {
        `when`(service.share("owner1", "user2", "snip1", "editor")).thenReturn(ShareResult.Success)

        val response = controller.share("snip1", ShareSnippetRequest("user2", "editor"), "owner1")

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(MemberResponse("snip1", "user2", "editor"), response.body)
    }

    @Test
    fun `share returns 403 FORBIDDEN when not allowed`() {
        `when`(service.share("viewer1", "user2", "snip1", "editor")).thenReturn(ShareResult.NotAllowed)

        val response = controller.share("snip1", ShareSnippetRequest("user2", "editor"), "viewer1")

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(ErrorResponse("not_allowed"), response.body)
    }

    @Test
    fun `share returns 409 CONFLICT when already member`() {
        `when`(service.share("owner1", "user2", "snip1", "editor")).thenReturn(ShareResult.AlreadyMember)

        val response = controller.share("snip1", ShareSnippetRequest("user2", "editor"), "owner1")

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorResponse("already_member"), response.body)
    }

    @Test
    fun `share returns 422 UNPROCESSABLE_ENTITY when role not found`() {
        `when`(service.share("owner1", "user2", "snip1", "invalid_role")).thenReturn(ShareResult.RoleNotFound)

        val response = controller.share("snip1", ShareSnippetRequest("user2", "invalid_role"), "owner1")

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        assertEquals(ErrorResponse("role_not_found"), response.body)
    }

    // --- changeRole ---

    @Test
    fun `changeRole returns 200 OK on success`() {
        `when`(service.changeRole("owner1", "user2", "snip1", "viewer")).thenReturn(ChangeRoleResult.Success)

        val response = controller.changeRole("snip1", "user2", ChangeRoleRequest("viewer"), "owner1")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(MemberResponse("snip1", "user2", "viewer"), response.body)
    }

    @Test
    fun `changeRole returns 403 FORBIDDEN when not allowed`() {
        `when`(service.changeRole("viewer1", "user2", "snip1", "viewer")).thenReturn(ChangeRoleResult.NotAllowed)

        val response = controller.changeRole("snip1", "user2", ChangeRoleRequest("viewer"), "viewer1")

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(ErrorResponse("not_allowed"), response.body)
    }

    @Test
    fun `changeRole returns 409 CONFLICT when modifying self`() {
        `when`(service.changeRole("owner1", "owner1", "snip1", "viewer")).thenReturn(ChangeRoleResult.CannotModifySelf)

        val response = controller.changeRole("snip1", "owner1", ChangeRoleRequest("viewer"), "owner1")

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorResponse("cannot_modify_self"), response.body)
    }

    @Test
    fun `changeRole returns 404 NOT_FOUND when member not found`() {
        `when`(service.changeRole("owner1", "user2", "snip1", "viewer")).thenReturn(ChangeRoleResult.MemberNotFound)

        val response = controller.changeRole("snip1", "user2", ChangeRoleRequest("viewer"), "owner1")

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorResponse("member_not_found"), response.body)
    }

    @Test
    fun `changeRole returns 422 UNPROCESSABLE_ENTITY when role not found`() {
        `when`(service.changeRole("owner1", "user2", "snip1", "invalid_role")).thenReturn(ChangeRoleResult.RoleNotFound)

        val response = controller.changeRole("snip1", "user2", ChangeRoleRequest("invalid_role"), "owner1")

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        assertEquals(ErrorResponse("role_not_found"), response.body)
    }

    // --- revoke ---

    @Test
    fun `revoke returns 204 NO_CONTENT on success`() {
        `when`(service.revoke("owner1", "user2", "snip1")).thenReturn(RevokeResult.Success)

        val response = controller.revoke("snip1", "user2", "owner1")

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `revoke returns 403 FORBIDDEN when not allowed`() {
        `when`(service.revoke("editor1", "user2", "snip1")).thenReturn(RevokeResult.NotAllowed)

        val response = controller.revoke("snip1", "user2", "editor1")

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(ErrorResponse("not_allowed"), response.body)
    }

    @Test
    fun `revoke returns 409 CONFLICT when revoking self`() {
        `when`(service.revoke("owner1", "owner1", "snip1")).thenReturn(RevokeResult.CannotModifySelf)

        val response = controller.revoke("snip1", "owner1", "owner1")

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorResponse("cannot_modify_self"), response.body)
    }

    @Test
    fun `revoke returns 404 NOT_FOUND when member not found`() {
        `when`(service.revoke("owner1", "user2", "snip1")).thenReturn(RevokeResult.MemberNotFound)

        val response = controller.revoke("snip1", "user2", "owner1")

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorResponse("member_not_found"), response.body)
    }
}
