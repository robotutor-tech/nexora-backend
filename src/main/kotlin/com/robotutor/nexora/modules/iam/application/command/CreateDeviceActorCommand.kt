package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.PremisesId

data class CreateDeviceActorCommand(
    val premisesId: PremisesId,
    val principal: DeviceContext
)