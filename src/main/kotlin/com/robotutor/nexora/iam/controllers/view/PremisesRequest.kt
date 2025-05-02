package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorIdentifier

data class PremisesRequest(val premisesId: PremisesId)
data class RegisterDeviceRequest(val deviceId: DeviceId, val type: ActorIdentifier)

data class RoleRequest(val name: String, val roleType: RoleType)


