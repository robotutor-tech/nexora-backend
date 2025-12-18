package com.robotutor.nexora.orchestration.client.view

import java.time.Instant

data class DeviceResponse(
    val deviceId: String,
    val premisesId: String,
    val name: String,
    val zoneId: String,
    val registeredBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
