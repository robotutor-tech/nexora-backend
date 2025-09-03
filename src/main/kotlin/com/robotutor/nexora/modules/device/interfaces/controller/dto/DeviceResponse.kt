package com.robotutor.nexora.modules.device.interfaces.controller.dto

import com.robotutor.nexora.modules.device.domain.entity.DeviceHealth
import com.robotutor.nexora.modules.device.domain.entity.DeviceState
import com.robotutor.nexora.modules.device.domain.entity.DeviceType

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