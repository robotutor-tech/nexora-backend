package com.robotutor.nexora.modules.device.interfaces.messaging.mapper

import com.robotutor.nexora.modules.device.interfaces.messaging.message.DeviceFeedsCreatedMessage
import com.robotutor.nexora.modules.device.domain.entity.FeedIds
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId

object DeviceMapper {
    fun toDeviceId(message: DeviceFeedsCreatedMessage): DeviceId {
        return DeviceId(value = message.deviceId)
    }

    fun toFeedIds(message: DeviceFeedsCreatedMessage): FeedIds {
        val feedIds = message.feedIds.map { feed -> FeedId(feed) }
        return FeedIds(feedIds)
    }
}