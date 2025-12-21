package com.robotutor.nexora.orchestration.controller.view

import com.robotutor.nexora.orchestration.client.view.DeviceResponse
import java.time.Instant

data class DeviceRegistrationResponse(
    val credentialId: String,
    val secret: String,
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
) {
    companion object {
        fun from(deviceResponse: DeviceResponse, credentialId: String, secret: String): DeviceRegistrationResponse {
            return DeviceRegistrationResponse(
                deviceId = deviceResponse.deviceId,
                credentialId = credentialId,
                secret = secret,
                premisesId = deviceResponse.premisesId,
                name = deviceResponse.name,
                zoneId = deviceResponse.zoneId,
                registeredBy = deviceResponse.registeredBy,
                createdAt = deviceResponse.createdAt,
                updatedAt = deviceResponse.updatedAt,
                accountId = deviceResponse.accountId,
                state = deviceResponse.state,
                health = deviceResponse.health,
                feeds = deviceResponse.feeds,
                metaData = deviceResponse.metaData
            )
        }
    }
}


data class DeviceMetaDataResponse(
    val osName: String,
    val osVersion: String,
    val modelNo: String,
    val serialNo: String,
)