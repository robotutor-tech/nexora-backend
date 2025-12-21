package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId

data class UpdateMetaDataCommand(
    val accountId: AccountId,
    val metadata: DeviceMetadata
) : Command
