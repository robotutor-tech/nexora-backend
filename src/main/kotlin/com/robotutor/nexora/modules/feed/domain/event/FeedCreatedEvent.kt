package com.robotutor.nexora.modules.feed.domain.event

import com.robotutor.nexora.modules.feed.domain.entity.FeedType
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

data class FeedCreatedEvent(
    val feedId: FeedId,
    val name: Name,
    val type: FeedType,
    val widgetType: WidgetType,
    val zoneId: ZoneId
) : FeedEvent("feed.created")
