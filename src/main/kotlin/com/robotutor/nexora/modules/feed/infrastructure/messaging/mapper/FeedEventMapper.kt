package com.robotutor.nexora.modules.feed.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.feed.domain.event.DeviceFeedsCreatedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedCreatedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.feed.infrastructure.messaging.message.DeviceFeedsCreatedMessage
import com.robotutor.nexora.modules.feed.infrastructure.messaging.message.FeedCreatedMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Service

@Service
class FeedEventMapper : EventMapper<FeedEvent> {
    override fun toEventMessage(event: FeedEvent): EventMessage {
        return when (event) {
            is DeviceFeedsCreatedEvent -> toDeviceFeedsCreatedMessage(event)
            is FeedCreatedEvent -> toFeedCreatedMessage(event)
        }
    }

    private fun toDeviceFeedsCreatedMessage(event: DeviceFeedsCreatedEvent): DeviceFeedsCreatedMessage {
        return DeviceFeedsCreatedMessage(
            deviceId = event.deviceId.value,
            feedIds = event.feedIds.map { it.value }
        )
    }

    private fun toFeedCreatedMessage(event: FeedCreatedEvent): FeedCreatedMessage {
        return FeedCreatedMessage(
            feedId = event.feedId.value,
            name = event.name.value,
            type = event.type,
            widgetType = event.widgetType,
            zoneId = event.zoneId.value
        )
    }
}