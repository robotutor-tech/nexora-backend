package com.robotutor.nexora.modules.widget.domain.model

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.WidgetId
import com.robotutor.nexora.shared.domain.model.ZoneId
import java.time.Instant

data class Widget(
    val widgetId: WidgetId,
    val premisesId: PremisesId,
    val name: Name,
    val feedId: FeedId,
    val zoneId: ZoneId,
    val type: WidgetType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate() {
    companion object {
        fun create(widgetId: WidgetId, createWidgetCommand: CreateWidgetCommand, actorData: ActorData): Widget {
            val widget = Widget(
                widgetId = widgetId,
                premisesId = actorData.premisesId,
                name = createWidgetCommand.name,
                feedId = createWidgetCommand.feedId,
                zoneId = createWidgetCommand.zoneId,
                type = createWidgetCommand.widgetType
            )
            widget.addDomainEvent(ResourceCreatedEvent(ResourceType.WIDGET, widget.widgetId))
            return widget
        }
    }
}

enum class WidgetType {
    TOGGLE,
    SLIDER
}

