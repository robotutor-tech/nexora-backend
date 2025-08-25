package com.robotutor.nexora.modules.iam.interfaces.controller

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.iam.application.ActorUseCase
import com.robotutor.nexora.modules.iam.application.CreateDeviceActorUseCase
import com.robotutor.nexora.modules.iam.application.RoleUseCase
import com.robotutor.nexora.modules.iam.application.command.CreateDeviceActorCommand
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/actors")
class ActorController(
    private val actorUseCase: ActorUseCase,
    private val roleUseCase: RoleUseCase,
    private val createDeviceActorUseCase: CreateDeviceActorUseCase
) {

    @GetMapping
    fun getActors(userData: UserData): Flux<ActorWithRolesResponse> {
        return actorUseCase.getActors(userData)
            .flatMap { actor ->
                roleUseCase.getRoles(actor.premisesId, actor.roleIds).collectList()
                    .map { roles -> ActorMapper.toActorWithRolesResponse(actor, roles) }
            }
    }

    @GetMapping("/{actorId}/roles/{roleId}")
    fun getActor(
        @PathVariable actorId: String,
        @PathVariable roleId: String,
    ): Mono<ActorResponse> {
        return actorUseCase.getActor(ActorId(actorId), RoleId(roleId))
            .flatMap { actor ->
                roleUseCase.getByRoleId(RoleId(roleId))
                    .map { role -> ActorMapper.toActorResponse(actor, role) }
            }
    }

    @GetMapping("me")
    fun getCurrentActor(actorData: ActorData): Mono<ActorResponse> {
        return createMono(actorData)
            .map { ActorMapper.toActorResponse(actorData) }
    }

    @PostMapping
    fun registerDeviceActor(deviceId: String, invitationData: InvitationData): Mono<ActorResponse> {
        val command = CreateDeviceActorCommand(
            premisesId = invitationData.premisesId,
            principal = DeviceContext(DeviceId(deviceId))
        )
        return createDeviceActorUseCase.register(command)
            .flatMap { actor ->
                roleUseCase.getByRoleId(actor.roleIds.first())
                    .map { role -> ActorMapper.toActorResponse(actor, role) }
            }
    }
}

