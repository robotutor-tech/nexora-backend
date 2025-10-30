package com.robotutor.nexora.modules.device.infrastructure.facade

import com.robotutor.nexora.modules.auth.interfaces.controller.AuthController
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthDeviceRegisterRequest
import com.robotutor.nexora.modules.device.application.facade.AuthDeviceFacade
import com.robotutor.nexora.modules.device.application.facade.dto.AuthDevice
import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthDeviceApiFacade(private val authController: AuthController) : AuthDeviceFacade {
    override fun register(device: Device, actorData: ActorData): Mono<AuthDevice> {
        val request = AuthDeviceRegisterRequest(
            deviceId = device.deviceId.value,
            actorId = actorData.actorId.value,
            roleId = actorData.role.roleId.value
        )
        return ContextDataResolver.getInvitationData()
            .flatMap { invitationData ->
                authController.registerDevice(request = request, invitationData = invitationData)
            }
            .map { AuthDevice(deviceId = it.deviceId, deviceSecret = it.deviceSecret) }
    }
}