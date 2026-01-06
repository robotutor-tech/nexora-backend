package com.robotutor.nexora.module.feed.infrastructure.messaging.mapper

import com.robotutor.nexora.common.message.mapper.EventMapper
import com.robotutor.nexora.common.message.message.EventMessage
import com.robotutor.nexora.module.feed.domain.event.FeedEvent
import com.robotutor.nexora.module.feed.domain.event.FeedRegisteredEvent
import com.robotutor.nexora.module.feed.domain.event.FeedValueUpdatedEvent
import com.robotutor.nexora.module.feed.infrastructure.messaging.message.FeedRegisteredEventMessage
import com.robotutor.nexora.module.feed.infrastructure.messaging.message.FeedValueUpdatedEventMessage

object FeedEventMapper : EventMapper<FeedEvent> {
    override fun toEventMessage(event: FeedEvent): EventMessage {
        return when (event) {
            is FeedRegisteredEvent -> FeedRegisteredEventMessage(event.feedId.value)
            is FeedValueUpdatedEvent -> FeedValueUpdatedEventMessage(event.feedId.value, event.value)
        }
    }
}