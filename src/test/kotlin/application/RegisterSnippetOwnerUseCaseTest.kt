package application

import age.of.printscript.demo.application.command.RegisterOwnerResult
import age.of.printscript.demo.application.command.RegisterSnippetOwnerUseCase
import support.FakeRoleRepository
import support.FakeSnippetMemberRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterSnippetOwnerUseCaseTest {
    private val members = FakeSnippetMemberRepository()

    private fun useCase(roleNames: Set<String> = setOf("OWNER")) = RegisterSnippetOwnerUseCase(members, FakeRoleRepository(roleNames))

    @Test
    fun `registers the owner when the snippet has no members`() {
        assertEquals(RegisterOwnerResult.Success, useCase()("user1", "snip1"))

        val saved = members.findBySnippetId("snip1")
        assertEquals(1, saved.size)
        assertEquals("user1", saved.first().userId)
        assertEquals("OWNER", saved.first().snippetRole.id)
    }

    @Test
    fun `fails when the snippet already has members`() {
        members.add("someone", "snip1", "OWNER")

        assertEquals(RegisterOwnerResult.AlreadyHasOwner, useCase()("user1", "snip1"))
        assertTrue(members.findBySnippetId("snip1").none { it.userId == "user1" })
    }

    @Test
    fun `fails when the OWNER role does not exist`() {
        assertEquals(RegisterOwnerResult.OwnerRoleMissing, useCase(roleNames = setOf("EDITOR"))("user1", "snip1"))
    }
}
