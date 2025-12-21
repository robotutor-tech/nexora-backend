package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class ActivateDeviceCommand(
    val premisesId: PremisesId,
    val deviceId: DeviceId,
    val accountId: AccountId,
    val accountType: AccountType,
    val metaData: DeviceMetadata
) : Command
