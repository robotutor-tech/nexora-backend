package com.robotutor.nexora.module.device.interfaces.controller.view

import com.robotutor.nexora.module.device.domain.aggregate.DeviceHealth
import com.robotutor.nexora.module.device.domain.aggregate.DeviceState
import java.time.Instant

data class DeviceResponse(
    val deviceId: String,
    val premisesId: String,
    val name: String,
    val state: DeviceState,
    val health: DeviceHealth,
    val feeds: List<String>,
    val metaData: DeviceMetaDataResponse?,
    val zoneId: String,
    val registeredBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class DeviceMetaDataResponse(
    val osName: String,
    val osVersion: String,
    val modelNo: String,
    val serialNo: String,
)