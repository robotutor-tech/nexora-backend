package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.aggregate.FeedType
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.FeedValueRange
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterFeedCommand(
    val premisesId: PremisesId,
    val deviceId: DeviceId,
    val type: FeedType,
    val range: FeedValueRange,
) : Command
