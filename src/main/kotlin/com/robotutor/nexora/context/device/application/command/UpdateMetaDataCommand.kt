package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorData

data class UpdateMetaDataCommand(
    val actorData: ActorData,
    val metadata: DeviceMetadata
) : Command
