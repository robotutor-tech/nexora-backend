package com.robotutor.nexora.modules.widget.domain.event

import com.robotutor.nexora.modules.widget.domain.entity.WidgetId

data class WidgetCreatedEvent(val widgetId: WidgetId) : WidgetEvent("widget.created")