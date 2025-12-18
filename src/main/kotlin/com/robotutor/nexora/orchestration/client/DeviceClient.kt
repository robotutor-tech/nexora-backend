package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.orchestration.client.view.DeviceResponse
import com.robotutor.nexora.orchestration.config.DeviceConfig
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeviceClient(
    private val webClient: WebClientWrapper,
    private val deviceConfig: DeviceConfig,
) {
    fun registerDevice(request: DeviceRegistrationRequest): Mono<DeviceResponse> {
        return webClient.post(
            baseUrl = deviceConfig.baseUrl,
            path = deviceConfig.path,
            body = mapOf("zoneId" to request.zoneId, "name" to request.name),
            returnType = DeviceResponse::class.java,
        )
    }
}