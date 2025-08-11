package com.robotutor.nexora.modules.orchestration.gateway

import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.orchestration.config.DeviceConfig
import com.robotutor.nexora.modules.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.shared.adapters.outbound.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeviceGateway(
    private val webClient: WebClientWrapper,
    private val deviceConfig: DeviceConfig,
) {

    fun registerDevice(request: DeviceRegistrationRequest, deviceType: DeviceType, feedCount: Int): Mono<DeviceView> {
        val body = mapOf(
            "modelNo" to request.modelNo,
            "serialNo" to request.serialNo,
            "deviceType" to deviceType,
            "feedCount" to feedCount,
        )

        return webClient.post(
            baseUrl = deviceConfig.baseUrl,
            path = deviceConfig.register,
            body = body,
            returnType = DeviceView::class.java,
        )
    }
}

data class DeviceView(
    val deviceId: String,
    val type: DeviceType,
)