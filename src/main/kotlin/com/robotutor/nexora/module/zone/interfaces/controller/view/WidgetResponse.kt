package com.robotutor.nexora.module.zone.interfaces.controller.view

import java.time.Instant

data class WidgetResponse(
    val widgetId: String,
    val feedId: String,
    val name: String,
    val metadata: WidgetMetadataResponse,
    val createdAt: Instant,
    val updatedAt: Instant,
)
