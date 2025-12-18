package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command

data class CompensateDeviceRegistrationCommand(val deviceId: DeviceId) : Command
