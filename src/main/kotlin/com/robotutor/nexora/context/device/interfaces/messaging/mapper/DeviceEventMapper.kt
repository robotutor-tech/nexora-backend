package com.robotutor.nexora.context.device.interfaces.messaging.mapper

import com.robotutor.nexora.context.device.application.command.CompensateDeviceRegistrationCommand
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.interfaces.messaging.message.CompensateDeviceRegistrationMessage

object DeviceEventMapper {
//    fun toDeviceId(message: DeviceFeedsCreatedMessage): DeviceId {
//        return DeviceId(value = message.deviceId)
//    }
//
//    fun toFeedIds(message: DeviceFeedsCreatedMessage): FeedIds {
//        val feedIds = message.feedIds.map { feed -> FeedId(feed) }
//        return FeedIds(feedIds)
//    }

    fun toCompensateDeviceRegistrationCommand(eventMessage: CompensateDeviceRegistrationMessage): CompensateDeviceRegistrationCommand {
        return CompensateDeviceRegistrationCommand(DeviceId(eventMessage.deviceId))
    }

}