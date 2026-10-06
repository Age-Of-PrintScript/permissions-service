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

@Entity
@Table(name = "roles")
class RoleEntity(
    // le dice a JPA que esta columna no se pasa en los metodos de repository
    // GenerationType es la forma en que se genera dicho id, identity usa la columna auto incremental de la base
    @Id // Id de la fila. Le clave este id si mas adelante un usuario puede tener mas de un rol.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column("user_id", nullable = false)
    val userId: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: SnippetPermissionType, // TODO(Implementar converter para la entidad asi se usa la interfaz en vez de el enum)
)
