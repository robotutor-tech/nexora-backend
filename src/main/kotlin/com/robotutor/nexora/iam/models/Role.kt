package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

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
    val policies: MutableSet<PolicyId> = mutableSetOf(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
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

    fun addPolicy(policies: List<Policy>): Role {
        this.policies.addAll(policies.map { it.policyId })
        return this
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