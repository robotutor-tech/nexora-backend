package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.modules.iam.domain.entity.EntitlementStatus
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType

data class EntitlementResponse(
    val entitlementId: String,
    val roleId: String,
    val premisesId: String,
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus
)