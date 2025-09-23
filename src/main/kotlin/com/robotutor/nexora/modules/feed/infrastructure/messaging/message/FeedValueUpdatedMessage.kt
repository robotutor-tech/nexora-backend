package com.robotutor.nexora.modules.feed.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class FeedValueUpdatedMessage(
    val feedId: String,
    val value: Int,
) : EventMessage
