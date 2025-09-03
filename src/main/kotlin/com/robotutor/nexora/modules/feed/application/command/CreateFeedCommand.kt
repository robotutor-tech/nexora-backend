package com.robotutor.nexora.modules.feed.application.command

import com.robotutor.nexora.modules.feed.domain.entity.FeedType
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.shared.domain.model.Name

data class CreateFeedCommand(
    val type: FeedType,
    val name: Name,
    val widgetType: WidgetType,
)
