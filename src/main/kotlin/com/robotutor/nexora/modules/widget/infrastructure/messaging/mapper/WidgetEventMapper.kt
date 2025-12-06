package com.robotutor.nexora.modules.widget.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.widget.domain.event.WidgetCreatedEvent
import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.modules.widget.infrastructure.messaging.message.WidgetCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object WidgetEventMapper : EventMapper<WidgetEvent> {
    override fun toEventMessage(event: WidgetEvent): EventMessage {
        return when (event) {
            is WidgetCreatedEvent -> toWidgetCreatedEventMessage(event)
        }
    }

    private fun toWidgetCreatedEventMessage(event: WidgetCreatedEvent): WidgetCreatedEventMessage {
        return WidgetCreatedEventMessage(event.widgetId.value)
    }
}
