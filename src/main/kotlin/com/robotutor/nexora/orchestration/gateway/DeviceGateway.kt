package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.DeviceConfig
import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.view.InvitationView
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeviceGateway(
    private val webClient: WebClientWrapper,
    private val deviceConfig: DeviceConfig,
) {

    fun registerDevice(invitation: InvitationView, request: DeviceRegistrationRequest): Mono<UserId> {
        val body = mapOf(
            "name" to invitation.name,
            "modelNo" to invitation.modelNo,
            "serialNo" to request.serialNo
        )

        return webClient.post(
            baseUrl = deviceConfig.baseUrl,
            path = deviceConfig.register,
            body = body,
            returnType = UserId::class.java,
        )
    }
}