package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.Entitlement
import com.robotutor.nexora.iam.models.EntitlementId
import com.robotutor.nexora.iam.models.EntitlementStatus
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType

data class EntitlementView(
    val entitlementId: EntitlementId,
    val roleId: RoleId,
    val premisesId: PremisesId,
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus
) {
    companion object {
        fun from(entitlement: Entitlement): EntitlementView {
            return EntitlementView(
                entitlementId = entitlement.entitlementId,
                roleId = entitlement.roleId,
                premisesId = entitlement.premisesId,
                action = entitlement.action,
                resourceType = entitlement.resourceType,
                resourceId = entitlement.resourceId,
                status = entitlement.status
            )
        }
    }
}