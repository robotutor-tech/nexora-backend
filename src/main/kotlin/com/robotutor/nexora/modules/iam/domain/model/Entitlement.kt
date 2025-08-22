package com.robotutor.nexora.modules.iam.domain.model

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import java.time.Instant

data class Entitlement(
    val entitlementId: EntitlementId,
    val roleId: RoleId,
    val premisesId: PremisesId,
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus = EntitlementStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null,
)

enum class EntitlementStatus {
    ACTIVE, INACTIVE
}

@JvmInline
value class EntitlementId(val value: String)