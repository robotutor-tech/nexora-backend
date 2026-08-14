package com.robotutor.nexora.module.feed.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface FeedEventMessage : EventMessage

data class FeedRegisteredEventMessage(val feedId: String) : FeedEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

data class FeedValueUpdatedEventMessage(val feedId: String, val value: Int) : FeedEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}
