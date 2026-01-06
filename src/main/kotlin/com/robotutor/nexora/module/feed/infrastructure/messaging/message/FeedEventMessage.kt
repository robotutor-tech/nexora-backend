package com.robotutor.nexora.module.feed.infrastructure.messaging.message

import com.robotutor.nexora.common.message.message.EventMessage

sealed class FeedEventMessage(name: String) : EventMessage(eventName = "feed.$name")

data class FeedRegisteredEventMessage(val feedId: String) : FeedEventMessage("registered")
data class FeedValueUpdatedEventMessage(val feedId: String, val value: Int) : FeedEventMessage("value.updated")