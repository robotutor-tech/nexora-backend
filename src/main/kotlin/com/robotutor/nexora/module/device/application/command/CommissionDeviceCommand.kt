package com.robotutor.nexora.module.device.application.command

import com.robotutor.nexora.module.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class CommissionDeviceCommand(
    val actorData: ActorData,
    val metadata: DeviceMetadata,
    val deviceId: DeviceId
) : Command
