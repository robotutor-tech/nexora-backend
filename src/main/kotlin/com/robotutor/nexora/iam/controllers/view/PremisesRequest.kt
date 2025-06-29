package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.iam.models.EntitlementId
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.ActorIdentifier

data class PremisesRequest(val premisesId: PremisesId)
data class RegisterDeviceRequest(val deviceId: DeviceId, val type: ActorIdentifier)

data class RoleRequest(val name: String, val roleType: RoleType)
data class EntitlementRequest(val action: ActionType, val resourceType: ResourceType)
data class RoleEntitlementRequest(val resourceId: String, val entitlementId: EntitlementId, val roleId: RoleId)

