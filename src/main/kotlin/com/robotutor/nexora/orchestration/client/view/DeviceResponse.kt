package com.robotutor.nexora.orchestration.client.view

import com.robotutor.nexora.orchestration.controller.view.DeviceMetaDataResponse
import java.time.Instant

data class DeviceResponse(
    val deviceId: String,
    val accountId: String,
    val premisesId: String,
    val name: String,
    val state: String,
    val health: String,
    val feeds: List<String>,
    val metaData: DeviceMetaDataResponse?,
    val zoneId: String,
    val registeredBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
