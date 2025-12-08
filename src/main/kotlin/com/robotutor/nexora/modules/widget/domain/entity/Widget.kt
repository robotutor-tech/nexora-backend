package com.robotutor.nexora.modules.widget.domain.entity

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.domain.event.WidgetCreatedEvent
import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.vo.Name
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
) : AggregateRoot<Widget, WidgetId, WidgetEvent>(widgetId) {
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
            widget.addEvent(WidgetCreatedEvent(widgetId = widget.widgetId))
            return widget
        }
    }
}

data class WidgetId(override val value: String) : SequenceId

enum class WidgetType {
    TOGGLE,
    SLIDER
}

