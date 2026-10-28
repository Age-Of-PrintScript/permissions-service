package age.of.printscript.demo.domain.port

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember

sealed interface UpdateMemberRoleOutcome { // Por ahi ponga esto en otro file, queda feo aca
    data object Updated : UpdateMemberRoleOutcome

    data object MemberNotFound : UpdateMemberRoleOutcome
}

sealed interface AddMemberOutcome {
    data object Added : AddMemberOutcome

    data object AlreadyMember : AddMemberOutcome
}

sealed interface RemoveMemberOutcome {
    data object Removed : RemoveMemberOutcome

    data object MemberNotFound : RemoveMemberOutcome
}

interface SnippetMemberRepository {
    fun findById(id: String): SnippetMember?

    fun findBySnippetId(snippetId: String): List<SnippetMember>

    fun updateRole(
        userId: String,
        snippetId: String,
        roleId: String,
    ): UpdateMemberRoleOutcome

    fun findPermissions(
        userId: String,
        snippetId: String,
    ): List<Permission>

    fun hasPermission(
        userId: String,
        snippetId: String,
        permission: String,
    ): Boolean

    fun delete(
        userId: String,
        snippetId: String,
    ): RemoveMemberOutcome

    fun add(
        userId: String,
        snippetId: String,
        roleId: String,
    ): AddMemberOutcome
}
