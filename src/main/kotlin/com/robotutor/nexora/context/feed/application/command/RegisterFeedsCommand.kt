package com.robotutor.nexora.context.feed.application.command

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterFeedsCommand(
    val premisesId: PremisesId,
    val deviceId: DeviceId,
    val modelNo: ModelNo
) : Command

