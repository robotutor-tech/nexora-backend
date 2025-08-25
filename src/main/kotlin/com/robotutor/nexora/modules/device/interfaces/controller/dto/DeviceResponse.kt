package com.robotutor.nexora.modules.device.interfaces.controller.dto

import com.robotutor.nexora.modules.device.domain.model.DeviceHealth
import com.robotutor.nexora.modules.device.domain.model.DeviceState
import com.robotutor.nexora.modules.device.domain.model.DeviceType

data class DeviceResponse(
    val deviceId: String,
    val premisesId: String,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val type: DeviceType,
    val state: DeviceState,
    val health: DeviceHealth,
    val feeds: List<String>,
)

data class DeviceTokensResponse(val token: String, val refreshToken: String)