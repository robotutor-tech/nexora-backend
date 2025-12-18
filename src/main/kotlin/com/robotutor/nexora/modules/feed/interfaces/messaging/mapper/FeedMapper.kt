package com.robotutor.nexora.modules.feed.interfaces.messaging.mapper

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.modules.feed.application.command.CreateDeviceFeedsCommand
import com.robotutor.nexora.modules.feed.interfaces.messaging.message.DeviceCreatedEventMessage
import com.robotutor.nexora.shared.domain.vo.ZoneId

object FeedMapper {
    fun toCreateDeviceFeedsCommand(event: DeviceCreatedEventMessage): CreateDeviceFeedsCommand {
        return CreateDeviceFeedsCommand(
            deviceId = DeviceId(event.deviceId),
            modelNo = ModelNo(event.modelNo),
            zoneId = ZoneId(event.zoneId)
        )
    }
}