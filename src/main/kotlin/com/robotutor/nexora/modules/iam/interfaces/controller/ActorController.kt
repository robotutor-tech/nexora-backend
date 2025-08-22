package com.robotutor.nexora.modules.iam.interfaces.controller

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.iam.application.ActorUseCase
import com.robotutor.nexora.modules.iam.application.RoleUseCase
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.shared.adapters.webclient.exceptions.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/actors")
class ActorController(private val actorUseCase: ActorUseCase, private val roleUseCase: RoleUseCase) {

    @GetMapping
    fun getActors(userData: UserData): Flux<ActorWithRolesResponse> {
        return actorUseCase.getActors(userData)
            .flatMap { actor ->
                roleUseCase.getRolesByRoleIds(actor.roleIds).collectList()
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
    fun getCurrentActor(actorData: ActorData): Mono<ActorData> {
        return createMono(actorData)
    }
}

