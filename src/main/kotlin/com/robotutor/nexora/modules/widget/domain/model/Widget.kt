package com.robotutor.nexora.modules.widget.domain.model

import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import com.robotutor.nexora.shared.domain.model.ZoneId
import java.time.Instant

data class Widget(
    val widgetId: WidgetId,
    val premisesId: PremisesId,
    val name: String,
    val feedId: FeedId,
    val zoneId: ZoneId,
    val type: WidgetType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
)

enum class WidgetType {
    TOGGLE,
    SLIDER
}

