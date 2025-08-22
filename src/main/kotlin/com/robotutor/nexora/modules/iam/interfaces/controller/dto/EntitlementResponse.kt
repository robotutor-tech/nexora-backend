package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.common.security.models.PremisesId
import com.robotutor.nexora.modules.iam.domain.model.EntitlementStatus
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType

data class EntitlementResponse(
    val entitlementId: String,
    val roleId: String,
    val premisesId: PremisesId,
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus
)