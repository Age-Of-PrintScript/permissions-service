package age.of.printscript.demo.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "user_snippet_roles",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "snippet_id"])],
    indexes = [Index(columnList = "user_id, snippet_id")],
)
class SnippetMemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Column(name = "snippet_id", nullable = false)
    val snippetId: String,
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    val role: RoleEntity,
)
