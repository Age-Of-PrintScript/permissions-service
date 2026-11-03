package age.of.printscript.demo.domain

// Modelo de permissions

data class Permission(
    val id: String,
    val name: String,
)

data class SnippetRole(
    val id: String,
    val name: String,
)

data class SnippetMember(
    val id: String? = null,
    val userId: String,
    val snippetId: String,
    val snippetRole: SnippetRole,
)
