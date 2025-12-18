package com.robotutor.nexora.context.device.infrastructure.facade

import com.robotutor.nexora.context.device.application.facade.AuthDeviceFacade
import com.robotutor.nexora.context.device.application.facade.dto.AuthDevice
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.iam.interfaces.controller.AccountController
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthDeviceApiFacade(private val authController: AccountController) : AuthDeviceFacade {
    override fun register(device: DeviceAggregate, actorData: ActorData): Mono<AuthDevice> {
        return Mono.empty()
//        val request = AuthDeviceRegisterRequest(
//            deviceId = device.deviceId.value,
//            actorId = actorData.actorId.value,
//            roleId = actorData.role.roleId.value
//        )
//        return ContextDataResolver.getInvitationData()
//            .flatMap { invitationData ->
////                authController.register(request = request, invitationData = invitationData)
//                Mono.empty()
//            }
////            .map { AuthDevice(deviceId = it.deviceId, deviceSecret = it.deviceSecret) }
    }
}