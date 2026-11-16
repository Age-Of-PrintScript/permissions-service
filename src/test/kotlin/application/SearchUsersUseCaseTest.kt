package application

import age.of.printscript.demo.application.query.SearchUsersUseCase
import age.of.printscript.demo.domain.DirectoryUser
import age.of.printscript.demo.domain.UserPage
import age.of.printscript.demo.domain.port.UserDirectory
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchUsersUseCaseTest {
    private class RecordingUserDirectory : UserDirectory {
        var lastQuery: String? = null
        var lastPage: Int? = null
        var lastPageSize: Int? = null
        var lastExcluded: String? = null

        override fun search(
            query: String?,
            page: Int,
            pageSize: Int,
            excludeUserId: String?,
        ): UserPage {
            lastQuery = query
            lastPage = page
            lastPageSize = pageSize
            lastExcluded = excludeUserId
            return UserPage(page, pageSize, 1, listOf(DirectoryUser("auth0|2", "pepe1")))
        }
    }

    @Test
    fun `normalizes query, clamps page and pageSize, and passes requesterId`() {
        val directory = RecordingUserDirectory()
        val useCase = SearchUsersUseCase(directory)

        val result = useCase(query = "  pepe  ", page = -2, pageSize = 200, requesterId = "auth0|1")

        assertEquals("pepe", directory.lastQuery)
        assertEquals(0, directory.lastPage)
        assertEquals(SearchUsersUseCase.MAX_PAGE_SIZE, directory.lastPageSize)
        assertEquals("auth0|1", directory.lastExcluded)
        assertEquals(1, result.total)
    }

    @Test
    fun `treats blank query as null`() {
        val directory = RecordingUserDirectory()
        SearchUsersUseCase(directory)(query = "   ", page = 0, pageSize = 5, requesterId = "auth0|1")
        assertEquals(null, directory.lastQuery)
    }
}
