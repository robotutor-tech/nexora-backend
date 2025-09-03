package com.robotutor.nexora.modules.widget.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class WidgetCreatedEventMessage(val widgetId: String) : EventMessage