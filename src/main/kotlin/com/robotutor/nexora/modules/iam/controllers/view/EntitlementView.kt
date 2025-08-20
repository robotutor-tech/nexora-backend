package com.robotutor.nexora.modules.iam.controllers.view

import com.robotutor.nexora.modules.iam.models.Entitlement
import com.robotutor.nexora.modules.iam.models.EntitlementId
import com.robotutor.nexora.modules.iam.models.EntitlementStatus
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.ResourceType

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