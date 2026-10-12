package age.of.printscript.demo.domain

// Modelo de permissions

data class Permission(
    val id: Long,
    val name: String,
)

data class Role(
    val id: Long,
    val name: String,
)

data class SnippetMember(
    val id: Long? = null,
    val userId: String,
    val snippetId: Long,
    val role: Role,
)
