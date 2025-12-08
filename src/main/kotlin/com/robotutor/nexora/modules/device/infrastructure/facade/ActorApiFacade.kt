//package com.robotutor.nexora.modules.device.infrastructure.facade
//
//import com.robotutor.nexora.modules.device.application.facade.ActorFacade
//import com.robotutor.nexora.modules.device.domain.entity.Device
//import com.robotutor.nexora.modules.iam.interfaces.controller.ActorController
//import com.robotutor.nexora.shared.application.service.ContextDataResolver
//import com.robotutor.nexora.shared.domain.model.*
//import com.robotutor.nexora.shared.domain.vo.Name
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class ActorApiFacade(private val actorController: ActorController) : ActorFacade {
//    override fun registerDeviceActor(device: Device): Mono<ActorData> {
//        return ContextDataResolver.getInvitationData()
//            .flatMap { invitationData ->
//                actorController.registerDeviceActor(device.deviceId.value, invitationData)
//            }
//            .map {
//                ActorData(
//                    actorId = ActorId(it.actorId),
//                    role = Role(
//                        roleId = RoleId(it.role.roleId),
//                        premisesId = device.premisesId,
//                        name = Name(it.role.name),
//                        roleType = it.role.roleType
//                    ),
//                    premisesId = device.premisesId,
//                    principalType = it.principalType,
//                    principal = DeviceData(
//                        deviceId = device.deviceId,
//                        premisesId = device.premisesId,
//                        name = device.name
//                    )
//                )
//            }
//    }
//}