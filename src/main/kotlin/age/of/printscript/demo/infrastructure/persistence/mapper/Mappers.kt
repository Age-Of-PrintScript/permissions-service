package age.of.printscript.demo.infrastructure.persistence.mapper

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.domain.SnippetRole
import age.of.printscript.demo.infrastructure.persistence.entity.PermissionEntity
import age.of.printscript.demo.infrastructure.persistence.entity.RoleEntity
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity

fun RoleEntity.toDomain() =
    SnippetRole(
        id = id!!,
        name = this.name,
    )

fun SnippetMemberEntity.toDomain() =
    SnippetMember(
        id = id!!,
        userId = userId,
        snippetId = snippetId,
        snippetRole = role.toDomain(),
    )

fun PermissionEntity.toDomain() =
    Permission(
        id = id!!,
        name = name,
    )
