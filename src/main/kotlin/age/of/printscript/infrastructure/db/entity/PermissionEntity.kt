package age.of.printscript.infrastructure.db.entity

import age.of.printscript.domain.SnippetPermissionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "permissions",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "snippet_id", "permission"])],
)
class PermissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val name: String,
    @Column(name = "snippet_id", nullable = false)
    val snippetID: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val permissionType: SnippetPermissionType, // TODO(implementar converter dentro de esta entity para usar interfaz en vez de el enum en si)
)
