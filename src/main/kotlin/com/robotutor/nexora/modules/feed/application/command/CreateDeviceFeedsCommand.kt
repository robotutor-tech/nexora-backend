package com.robotutor.nexora.modules.feed.application.command

import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.ZoneId

data class CreateDeviceFeedsCommand(
    val deviceId: DeviceId, val modelNo: ModelNo, val zoneId: ZoneId
)
