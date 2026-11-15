package age.of.printscript.demo.infrastructure.controller.dto

import jakarta.validation.constraints.NotBlank

data class ShareSnippetRequest(
    @field:NotBlank val userId: String,
    @field:NotBlank val role: String,
)

data class ChangeRoleRequest(
    @field:NotBlank val role: String,
)

data class MemberResponse(
    val snippetId: String,
    val userId: String,
    val role: String,
)

data class ErrorResponse(
    val error: String,
)
