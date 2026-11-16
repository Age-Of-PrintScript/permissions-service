package age.of.printscript.demo.domain

data class DirectoryUser(
    val id: String,
    val name: String,
)

data class UserPage(
    val page: Int,
    val pageSize: Int,
    val total: Long,
    val users: List<DirectoryUser>,
)
