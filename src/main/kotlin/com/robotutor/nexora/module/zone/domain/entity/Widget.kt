package com.robotutor.nexora.module.zone.domain.entity

import com.robotutor.nexora.module.zone.domain.vo.WidgetId
import com.robotutor.nexora.module.zone.domain.vo.WidgetMetadata
import com.robotutor.nexora.shared.domain.Entity
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

class Widget private constructor(
    val widgetId: WidgetId,
    val name: Name,
    val feedId: FeedId,
    val metadata: WidgetMetadata,
    val createdAt: Instant,
    val updatedAt: Instant,
) : Entity<Widget, WidgetId>(widgetId) {
    companion object {
        fun create(
            widgetId: WidgetId,
            name: Name,
            feedId: FeedId,
            metadata: WidgetMetadata,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): Widget {
            return Widget(widgetId, name, feedId, metadata, createdAt, updatedAt)
        }

        fun register(name: Name, feedId: FeedId, metadata: WidgetMetadata): Widget {
            return create(WidgetId.generate(), name, feedId, metadata)
        }
    }
}