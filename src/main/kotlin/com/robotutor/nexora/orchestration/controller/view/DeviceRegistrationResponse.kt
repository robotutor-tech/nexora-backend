package com.robotutor.nexora.orchestration.controller.view

import com.robotutor.nexora.orchestration.client.view.DeviceResponse
import java.time.Instant

data class DeviceRegistrationResponse(
    val deviceId: String,
    val secret: String,
    val premisesId: String,
    val name: String,
    val zoneId: String,
    val registeredBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(deviceResponse: DeviceResponse, secret: String): DeviceRegistrationResponse {
            return DeviceRegistrationResponse(
                deviceId = deviceResponse.deviceId,
                secret = secret,
                premisesId = deviceResponse.premisesId,
                name = deviceResponse.name,
                zoneId = deviceResponse.zoneId,
                registeredBy = deviceResponse.registeredBy,
                createdAt = deviceResponse.createdAt,
                updatedAt = deviceResponse.updatedAt
            )
        }
    }
}

