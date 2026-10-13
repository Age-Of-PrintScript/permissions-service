package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.GetUserPermissionsUseCase
import age.of.printscript.demo.infrastructure.controller.dto.PermissionResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PermissionController(
    private val getUserPermissions: GetUserPermissionsUseCase,
) {
    @GetMapping("/permissions")
    fun list(
        @RequestParam userId: String,
        @RequestParam snippetId: Long,
    ): List<PermissionResponse> = getUserPermissions.invoke(userId, snippetId).map { PermissionResponse(it.id, it.name) }
}
