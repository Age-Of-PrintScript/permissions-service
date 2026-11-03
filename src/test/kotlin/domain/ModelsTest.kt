package domain

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.SnippetRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelsTest {
    @Test
    fun `models keep their values and support equality`() {
        val snippetRole = SnippetRole("1", "OWNER")
        assertEquals(Permission("1", "READ"), Permission("1", "READ"))
        assertEquals(snippetRole, SnippetRole("1", "OWNER"))
        val member = SnippetMember(userId = "ana", snippetId = "10", snippetRole = snippetRole)
        assertNull(member.id)
        assertEquals("5", member.copy(id = "5").id)
    }
}
