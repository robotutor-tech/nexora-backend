package com.robotutor.nexora.modules.iam.adapters.model

import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.RoleType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ROLE_COLLECTION = "roles"

@TypeAlias("Role")
@Document(ROLE_COLLECTION)
data class RoleDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val roleId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val roleType: RoleType,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?
) {
    fun toDomainModel(): Role {
        return Role(
            roleId = RoleId(roleId),
            premisesId = PremisesId(premisesId),
            name = Name(name),
            roleType = roleType,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version
        )
    }

    companion object {
        fun from(role: Role): RoleDocument {
            return RoleDocument(
                roleId = role.roleId.value,
                premisesId = role.premisesId.value,
                name = role.name.value,
                roleType = role.roleType,
                createdAt = role.createdAt,
                updatedAt = role.updatedAt,
                version = role.version,
            )
        }
    }
}
