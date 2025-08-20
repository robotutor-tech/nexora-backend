package com.robotutor.nexora.modules.iam.models

import com.robotutor.nexora.modules.iam.controllers.view.EntitlementRequest
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ENTITLEMENT_COLLECTION = "entitlements"

@TypeAlias("Entitlement")
@Document(ENTITLEMENT_COLLECTION)
data class Entitlement(
    @Id val id: String? = null,
    @Indexed(unique = true)
    val entitlementId: EntitlementId,
    @Indexed
    val roleId: RoleId,
    @Indexed
    val premisesId: PremisesId,
    @Indexed
    val action: ActionType,
    @Indexed
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus = EntitlementStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long = 0,
) {
    companion object {
        fun from(entitlementId: EntitlementId, premisesId: PremisesId, request: EntitlementRequest): Entitlement {
            return Entitlement(
                entitlementId = entitlementId,
                action = request.action,
                premisesId = premisesId,
                resourceType = request.resourceType,
                roleId = request.roleId,
                resourceId = request.resourceId,
            )
        }
    }
}

enum class EntitlementStatus {
    DRAFT,
    ACTIVE,
    REVOKED,
    EXPIRED
}

typealias EntitlementId = String