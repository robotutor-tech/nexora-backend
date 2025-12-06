package com.robotutor.nexora.modules.feed.infrastructure.messaging.message

import com.robotutor.nexora.modules.feed.domain.entity.FeedType
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class FeedCreatedMessage(
    val feedId: String,
    val name: String,
    val type: FeedType,
    val widgetType: WidgetType,
    val zoneId: String
) : EventMessage("feed.created")
