package age.of.printscript.demo.infrastructure.controller

import age.of.printscript.demo.application.query.GetUserPermissionsUseCase
import age.of.printscript.demo.infrastructure.controller.dto.PermissionResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PermissionController(
    private val getUserPermissions: GetUserPermissionsUseCase,
) {
    private val log = LoggerFactory.getLogger(PermissionController::class.java)

    @GetMapping("/permissions")
    fun list(
        @RequestParam userId: String,
        @RequestParam snippetId: String,
    ): List<PermissionResponse> {
        val permissions = getUserPermissions(userId, snippetId)
        log.info("Listed {} permissions for user {} on snippet {}", permissions.size, userId, snippetId)
        return permissions.map { PermissionResponse(it.id, it.name) }
    }
}
