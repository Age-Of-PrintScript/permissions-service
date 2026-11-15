package infrastructure.controller

import age.of.printscript.demo.application.command.RegisterOwnerResult
import age.of.printscript.demo.application.command.RegisterSnippetOwnerUseCase
import age.of.printscript.demo.infrastructure.controller.SnippetOwnerController
import age.of.printscript.demo.infrastructure.controller.dto.ErrorResponse
import age.of.printscript.demo.infrastructure.controller.dto.MemberResponse
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.test.Test
import kotlin.test.assertEquals

class SnippetOwnerControllerTest {
    private val useCase = mock(RegisterSnippetOwnerUseCase::class.java)
    private val controller = SnippetOwnerController(useCase)

    private fun jwt(subject: String): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "none")
            .subject(subject)
            .build()

    @Test
    fun `returns 201 CREATED on success`() {
        `when`(useCase("user1", "snip1")).thenReturn(RegisterOwnerResult.Success)

        val response = controller.register("snip1", jwt("user1"))

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(MemberResponse("snip1", "user1", "OWNER"), response.body)
    }

    @Test
    fun `returns 409 CONFLICT when the snippet already has an owner`() {
        `when`(useCase("user1", "snip1")).thenReturn(RegisterOwnerResult.AlreadyHasOwner)

        val response = controller.register("snip1", jwt("user1"))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorResponse("already_has_owner"), response.body)
    }

    @Test
    fun `returns 500 when the OWNER role is missing`() {
        `when`(useCase("user1", "snip1")).thenReturn(RegisterOwnerResult.OwnerRoleMissing)

        val response = controller.register("snip1", jwt("user1"))

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(ErrorResponse("owner_role_missing"), response.body)
    }
}
