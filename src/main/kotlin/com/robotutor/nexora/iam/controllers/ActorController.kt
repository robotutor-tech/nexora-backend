package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorWithRoleView
import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.EntitlementService
import com.robotutor.nexora.iam.services.RoleEntitlementService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(private val actorService: ActorService, private val roleService: RoleService) {
    @GetMapping
    fun getActors(authUserData: AuthUserData): Flux<ActorView> {
        return actorService.getActors(authUserData).flatMap { createActorView(it) }
    }

    @GetMapping("/{actorId}")
    fun getActorWithEntitlements(
        @PathVariable actorId: ActorId,
        @RequestParam roleId: RoleId = ""
    ): Mono<ActorWithRoleView> {
        return actorService.getActor(actorId, roleId)
    }


    private fun createActorView(actor: Actor): Mono<ActorView> {
        return roleService.getRolesByRoleIds(actor.roles.toList())
            .collectList()
            .map { roles -> ActorView.from(actor, roles) }
    }
}

