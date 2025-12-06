package com.robotutor.nexora.modules.widget.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class WidgetCreatedEventMessage(val widgetId: String) : EventMessage("widget.created")