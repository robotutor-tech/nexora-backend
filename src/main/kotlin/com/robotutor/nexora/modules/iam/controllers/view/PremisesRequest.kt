package com.robotutor.nexora.modules.iam.controllers.view

import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.shared.domain.model.ActorIdentifier

data class PremisesRequest(val premisesId: PremisesId)
data class RegisterDeviceRequest(val deviceId: String, val type: ActorIdentifier)

data class RoleRequest(val name: String, val roleType: RoleType)
data class EntitlementRequest(
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val roleId: RoleId
)


