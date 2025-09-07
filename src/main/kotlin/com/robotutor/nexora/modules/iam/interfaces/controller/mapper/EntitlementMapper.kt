package com.robotutor.nexora.modules.iam.interfaces.controller.mapper

import com.robotutor.nexora.modules.iam.domain.entity.Entitlement
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.EntitlementResponse

object EntitlementMapper {
        fun toEntitlementResponse(entitlement: Entitlement): EntitlementResponse {
            return EntitlementResponse(
                entitlementId = entitlement.entitlementId.value,
                roleId = entitlement.roleId.value,
                premisesId = entitlement.premisesId.value,
                action = entitlement.action,
                resourceType = entitlement.resourceType,
                resourceId = entitlement.resourceId.value,
                status = entitlement.status
            )
        }
    }
