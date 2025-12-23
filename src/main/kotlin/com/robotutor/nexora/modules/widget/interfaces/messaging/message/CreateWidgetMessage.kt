package com.robotutor.nexora.modules.widget.interfaces.messaging.message

import com.robotutor.nexora.context.device.domain.aggregate.FeedType
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType

data class CreateWidgetMessage(
    val feedId: String,
    val name: String,
    val type: FeedType,
    val widgetType: WidgetType,
    val zoneId: String
)
