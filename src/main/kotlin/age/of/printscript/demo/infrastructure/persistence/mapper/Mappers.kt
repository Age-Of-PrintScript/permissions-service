package age.of.printscript.demo.infrastructure.persistence.mapper

import age.of.printscript.demo.domain.Role
import age.of.printscript.demo.domain.SnippetMember
import age.of.printscript.demo.infrastructure.persistence.entity.SnippetMemberEntity
import age.of.printscript.demo.infrastructure.persistence.entity.UserSnippetRoleEntity

fun UserSnippetRoleEntity.toDomain() =
    Role(
        id = id!!,
        name = this.name,
    )

fun SnippetMemberEntity.toDomain() =
    SnippetMember(
        id = id!!,
        userId = userId,
        snippetId = snippetId,
        role = role.toDomain(),
    )
