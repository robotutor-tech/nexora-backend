package com.robotutor.nexora.modules.iam.domain.entity

import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.SequenceId
import java.time.Instant

data class Entitlement(
    val entitlementId: EntitlementId,
    val roleId: RoleId,
    val premisesId: PremisesId,
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: ResourceId,
    val status: EntitlementStatus = EntitlementStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null,
) : DomainAggregate<DomainEvent>() {
    companion object {
        fun create(
            entitlementId: EntitlementId,
            roleId: RoleId,
            premisesId: PremisesId,
            action: ActionType,
            resourceType: ResourceType,
            resourceId: ResourceId
        ): Entitlement {
            return Entitlement(
                entitlementId = entitlementId,
                roleId = roleId,
                premisesId = premisesId,
                action = action,
                resourceType = resourceType,
                resourceId = resourceId,
            )
        }
    }
}

enum class EntitlementStatus {
    ACTIVE, INACTIVE
}

class EntitlementId(override val value: String) : SequenceId