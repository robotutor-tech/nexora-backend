package com.robotutor.nexora.modules.feed.application.command

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.domain.vo.ZoneId

data class CreateDeviceFeedsCommand(
    val deviceId: DeviceId, val modelNo: ModelNo, val zoneId: ZoneId
)
