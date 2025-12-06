package com.robotutor.nexora.modules.feed.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class FeedValueUpdatedMessage(
    val feedId: String,
    val value: Int,
) : EventMessage("value.updated")
