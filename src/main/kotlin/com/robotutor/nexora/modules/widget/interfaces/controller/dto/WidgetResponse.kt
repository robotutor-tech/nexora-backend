package com.robotutor.nexora.modules.widget.interfaces.controller.dto

import com.robotutor.nexora.modules.widget.domain.model.WidgetType

data class WidgetResponse(
    val widgetId: String,
    val premisesId: String,
    val name: String,
    val feedId: String,
    val type: WidgetType,
    val zoneId: String,
)