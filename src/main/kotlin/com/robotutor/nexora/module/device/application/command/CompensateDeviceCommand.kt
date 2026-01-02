package com.robotutor.nexora.module.device.application.command

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command

data class CompensateDeviceCommand(val deviceId: DeviceId) : Command
