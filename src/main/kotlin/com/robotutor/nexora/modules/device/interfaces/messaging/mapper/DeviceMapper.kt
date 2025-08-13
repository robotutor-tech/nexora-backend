package com.robotutor.nexora.modules.device.interfaces.messaging.mapper

import com.robotutor.nexora.modules.device.interfaces.messaging.dto.UpdateFeedsDto
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId

object DeviceMapper {
    fun toDeviceId(message: UpdateFeedsDto): DeviceId {
        return DeviceId(value = message.deviceId)
    }

    fun toFeedIds(message: UpdateFeedsDto): FeedIds {
        val feedIds = message.feeds.map { feed -> FeedId(feed) }
        return FeedIds(feedIds)
    }
}