package application

import age.of.printscript.demo.application.query.GetUserSnippetsUseCase
import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Permissions
import age.of.printscript.demo.domain.SnippetRole
import support.FakeSnippetMemberRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUserSnippetsUseCaseTest {
    @Test
    fun `devuelve snippets propios y compartidos donde el usuario tiene snippet read`() {
        val owner = SnippetRole("r1", "OWNER")
        val viewer = SnippetRole("r2", "VIEWER")
        val noRead = SnippetRole("r3", "BLIND")
        val repo =
            FakeSnippetMemberRepository().apply {
                givenRolePermissions(owner, listOf(Permission("p1", Permissions.READ), Permission("p2", Permissions.SHARE)))
                givenRolePermissions(viewer, listOf(Permission("p1", Permissions.READ)))
                givenRolePermissions(noRead, listOf(Permission("p3", Permissions.WRITE)))
                add("alice", "s1", owner.id)
                add("alice", "s2", viewer.id)
                add("alice", "s3", noRead.id)
                add("bob", "s4", owner.id)
            }

        val result = GetUserSnippetsUseCase(repo)("alice")

        assertEquals(listOf("s1" to "OWNER", "s2" to "VIEWER"), result.map { it.snippetId to it.snippetRole.name })
    }

    @Test
    fun `devuelve lista vacia cuando el usuario no tiene acceso a ningun snippet`() {
        val result = GetUserSnippetsUseCase(FakeSnippetMemberRepository())("alice")
        assertTrue(result.isEmpty())
    }
}
