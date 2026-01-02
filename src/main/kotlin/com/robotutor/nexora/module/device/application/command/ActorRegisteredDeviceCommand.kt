package com.robotutor.nexora.module.device.application.command

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class ActorRegisteredDeviceCommand(val deviceId: DeviceId, val premisesId: PremisesId) : Command
