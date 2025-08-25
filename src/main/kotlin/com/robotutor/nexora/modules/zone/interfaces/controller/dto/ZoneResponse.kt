package com.robotutor.nexora.modules.zone.interfaces.controller.dto

import java.time.Instant

data class ZoneResponse(
    val zoneId: String,
    val premisesId: String,
    val name: String,
    val createdAt: Instant
)
