package age.of.printscript.demo.infrastructure.controller.dto

import jakarta.validation.constraints.NotBlank

data class ShareSnippetRequest(
    @field:NotBlank val userId: String,
    @field:NotBlank val roleId: String,
)

data class ChangeRoleRequest(
    @field:NotBlank val roleId: String,
)

data class MemberResponse(
    val snippetId: String,
    val userId: String,
    val roleId: String,
)

data class ErrorResponse(
    val error: String,
)
