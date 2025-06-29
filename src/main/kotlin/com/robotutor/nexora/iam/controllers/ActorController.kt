package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.controllers.view.ActorWithRolesView
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(private val actorService: ActorService, private val roleService: RoleService) {

    @GetMapping
    fun getActors(authUserData: AuthUserData): Flux<ActorWithRolesView> {
        return actorService.getActors(authUserData).flatMap { createActorView(it) }
    }

    @GetMapping("/{actorId}/roles/{roleId}")
    fun getActorWithEntitlements(
        @PathVariable actorId: ActorId,
        @PathVariable roleId: RoleId,
    ): Mono<ActorView> {
        return actorService.getActor(actorId, roleId)
            .flatMap { actor ->
                roleService.getRoleByRoleId(roleId)
                    .map { ActorView.from(actor, it) }
            }
    }


    private fun createActorView(actor: Actor): Mono<ActorWithRolesView> {
        return roleService.getRolesByRoleIds(actor.roles.toList())
            .collectList()
            .map { roles -> ActorWithRolesView.from(actor, roles) }
    }
}

