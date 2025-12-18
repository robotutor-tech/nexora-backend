package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId

data class CommissionDeviceCommand(val accountId: AccountId, val deviceId: DeviceId) : Command
