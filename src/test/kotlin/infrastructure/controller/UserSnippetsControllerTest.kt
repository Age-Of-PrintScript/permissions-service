package infrastructure.controller

import age.of.printscript.demo.application.query.GetUserSnippetsUseCase
import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Permissions
import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.infrastructure.controller.UserSnippetsController
import age.of.printscript.demo.infrastructure.controller.dto.AccessibleSnippetResponse
import org.springframework.security.oauth2.jwt.Jwt
import support.FakeSnippetMemberRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class UserSnippetsControllerTest {
    private fun jwt(subject: String): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "none")
            .subject(subject)
            .build()

    @Test
    fun `list maps accessible snippets for the jwt subject`() {
        val owner = SnippetRole("1", "OWNER")
        val editor = SnippetRole("2", "EDITOR")
        val repo =
            FakeSnippetMemberRepository().apply {
                givenRolePermissions(owner, listOf(Permission("1", Permissions.READ)))
                givenRolePermissions(editor, listOf(Permission("1", Permissions.READ)))
                add("auth0|ana", "s1", owner.id)
                add("auth0|ana", "s2", editor.id)
                add("auth0|bob", "s3", owner.id)
            }
        val controller = UserSnippetsController(GetUserSnippetsUseCase(repo))

        val result = controller.list(jwt("auth0|ana"))

        assertEquals(
            listOf(
                AccessibleSnippetResponse("s1", "OWNER"),
                AccessibleSnippetResponse("s2", "EDITOR"),
            ),
            result,
        )
    }

    @Test
    fun `list returns empty when user has no accessible snippets`() {
        val controller = UserSnippetsController(GetUserSnippetsUseCase(FakeSnippetMemberRepository()))
        assertEquals(emptyList(), controller.list(jwt("auth0|nadie")))
    }
}
