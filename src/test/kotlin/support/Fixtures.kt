package support

import age.of.printscript.demo.domain.Permission
import age.of.printscript.demo.domain.Permissions
import age.of.printscript.demo.domain.SnippetRole

object Fixtures {
    const val SNIPPET = "s1"

    // roles
    const val OWNER = "owner"
    const val EDITOR = "editor"
    const val VIEWER = "viewer"

    // usuarios
    const val ALICE = "alice" // owner
    const val BOB = "bob" // editor
    const val CAROL = "carol" // viewer
    const val DAVE = "dave" // no es miembro

    val rolePermissions: Map<String, Set<String>> =
        mapOf(
            OWNER to setOf(Permissions.READ, Permissions.WRITE, Permissions.SHARE),
            EDITOR to setOf(Permissions.READ, Permissions.WRITE),
            VIEWER to setOf(Permissions.READ),
        )

    /** Cada par es (userId, roleId), todos sobre SNIPPET. */
    fun memberRepo(members: List<Pair<String, String>>): FakeSnippetMemberRepository {
        val repo = FakeSnippetMemberRepository()

        rolePermissions.forEach { (roleId, permissions) ->
            repo.givenRolePermissions(
                snippetRole = SnippetRole(id = roleId, name = roleId),
                permissions = permissions.map { Permission(id = it, name = it) },
            )
        }
        members.forEach { (userId, roleId) -> repo.add(userId, SNIPPET, roleId) }

        return repo
    }

    fun roleRepo() = FakeRoleRepository(rolePermissions.keys)
}
