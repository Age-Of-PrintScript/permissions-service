package age.of.printscript.demo.application.query

import age.of.printscript.demo.domain.UserPage
import age.of.printscript.demo.domain.port.UserDirectory
import org.springframework.stereotype.Service

@Service
class SearchUsersUseCase(
    private val users: UserDirectory,
) {
    operator fun invoke(
        query: String?,
        page: Int,
        pageSize: Int,
        requesterId: String?,
    ): UserPage {
        val safePage = page.coerceAtLeast(0)
        val safeSize = pageSize.coerceIn(1, MAX_PAGE_SIZE)
        val normalizedQuery = query?.trim()?.takeIf { it.isNotEmpty() }
        return users.search(normalizedQuery, safePage, safeSize, requesterId)
    }

    companion object {
        const val MAX_PAGE_SIZE = 50
    }
}
