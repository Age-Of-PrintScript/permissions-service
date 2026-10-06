package age.of.printscript.domain

sealed interface PermissionType {
    val name: String
}

sealed interface RoleType {
    val name: String
}

enum class SnippetRoleType : PermissionType { ADMIN, DEVELOPER } // 'todos los users son developers' -- Mati Brégoli

enum class SnippetPermissionType : RoleType { READ, WRITE, EXECUTE }

// Los pongo juntos por SRP, esto tiene tan solo una razon para cambiar, que es para que se expanda el dominio de mis permisos
