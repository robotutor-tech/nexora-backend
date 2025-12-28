package com.robotutor.nexora.context.zone.interfaces.controller.view

import java.time.Instant

data class WidgetResponse(
    val widgetId: String,
    val feedId: String,
    val name: String,
    val metadata: WidgetMetadataResponse,
    val createdAt: Instant,
    val updatedAt: Instant,
)
