package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.UserPage

class UserDirectoryUnavailableException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

interface UserDirectory {
    fun search(
        query: String?,
        page: Int,
        pageSize: Int,
        excludeUserId: String?,
    ): UserPage
}
