package age.of.printscript.demo.infrastructure.controller.dto

// data class GrantPermissionRequest(
// La url del snippet viaja en la url de la request, no en el body
//  @field:NotBlank val userId: String,
// @field:NotBlank val permission: String,
// )

data class PermissionResponse(
    val id: Long,
    val name: String,
)
