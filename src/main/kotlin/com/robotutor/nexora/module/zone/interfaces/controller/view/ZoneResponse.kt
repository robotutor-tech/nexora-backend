package com.robotutor.nexora.module.zone.interfaces.controller.view

import java.time.Instant

data class ZoneResponse(
    val zoneId: String,
    val premisesId: String,
    val name: String,
    val widgets: List<WidgetResponse>,
    val createdAt: Instant,
    val updatedAt: Instant
)
