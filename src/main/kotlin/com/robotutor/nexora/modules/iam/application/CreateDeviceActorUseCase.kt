//package com.robotutor.nexora.modules.iam.application
//
//import com.robotutor.nexora.modules.iam.application.command.CreateActorCommand
//import com.robotutor.nexora.modules.iam.application.command.CreateDeviceActorCommand
//import com.robotutor.nexora.modules.iam.application.command.CreateRoleCommand
//import com.robotutor.nexora.modules.iam.domain.entity.Actor
//import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
//import com.robotutor.nexora.shared.domain.vo.Name
//import com.robotutor.nexora.shared.domain.model.RoleType
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class CreateDeviceActorUseCase(private val roleUseCase: RoleUseCase, private val actorUseCase: ActorUseCase) {
//
//    fun register(command: CreateDeviceActorCommand): Mono<Actor> {
//        val createRoleCommand = CreateRoleCommand(command.premisesId, name = Name("Device"), roleType = RoleType.DEVICE)
//        return roleUseCase.createRole(createRoleCommand)
//            .flatMap { role ->
//                val createActorCommand = CreateActorCommand(
//                    premisesId = role.premisesId,
//                    roles = listOf(role.roleId),
//                    principalType = ActorPrincipalType.DEVICE,
//                    principal = command.principal
//                )
//                actorUseCase.createActor(createActorCommand)
//            }
//    }
//}