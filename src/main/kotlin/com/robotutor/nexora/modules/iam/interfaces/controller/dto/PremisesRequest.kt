package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.RoleType

data class PremisesRequest(val premisesId: String)
data class RegisterDeviceRequest(val deviceId: String, val type: ActorPrincipalType)

data class RoleRequest(val name: String, val roleType: RoleType)
data class EntitlementRequest(
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: String,
    val roleId: RoleId
)


