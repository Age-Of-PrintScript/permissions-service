package age.of.printscript.demo.infrastructure.controller.dto

data class PermissionResponse(
    val id: String,
    val name: String,
)

data class AccessibleSnippetResponse(
    val snippetId: String,
    val role: String,
)
