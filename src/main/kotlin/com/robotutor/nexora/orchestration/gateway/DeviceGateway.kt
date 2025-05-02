package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.device.controllers.view.DeviceView
import com.robotutor.nexora.device.models.DeviceType
import com.robotutor.nexora.orchestration.config.DeviceConfig
import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeviceGateway(
    private val webClient: WebClientWrapper,
    private val deviceConfig: DeviceConfig,
) {

    fun registerDevice(request: DeviceRegistrationRequest, deviceType: DeviceType): Mono<DeviceView> {
        val body = mapOf(
            "modelNo" to request.modelNo,
            "serialNo" to request.serialNo,
            "deviceType" to deviceType
        )

        return webClient.post(
            baseUrl = deviceConfig.baseUrl,
            path = deviceConfig.register,
            body = body,
            returnType = DeviceView::class.java,
        )
    }
}