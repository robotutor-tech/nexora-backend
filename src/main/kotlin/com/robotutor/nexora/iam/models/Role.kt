package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.ZoneOffset

const val ROLE_COLLECTION = "roles"

@TypeAlias("Role")
@Document(ROLE_COLLECTION)
data class Role(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val roleId: RoleId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(
            roleId: RoleId,
            premisesId: PremisesId,
            roleRequest: RoleRequest,
        ): Role {
            return Role(
                roleId = roleId,
                premisesId = premisesId,
                name = roleRequest.name,
                role = roleRequest.roleType,
            )
        }
    }
}

enum class RoleType {
    BOT,
    CUSTOM,
    GUEST,
    USER,
    ADMIN,
    OWNER,
}

typealias RoleId = String