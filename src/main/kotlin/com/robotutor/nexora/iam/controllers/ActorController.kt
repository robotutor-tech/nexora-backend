package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.controllers.view.RegisterActorsRequest
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.PolicyService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(
    private val actorService: ActorService,
    private val roleService: RoleService,
    private val policyService: PolicyService
) {

    @PostMapping
    fun registerActors(
        @RequestBody @Validated request: RegisterActorsRequest,
        authUserData: AuthUserData
    ): Flux<ActorView> {
        return actorService.registerActors(request, authUserData).flatMap { createActorView(it) }
    }

    @GetMapping
    fun getActors(authUserData: AuthUserData): Flux<ActorView> {
        return actorService.getActors(authUserData).flatMap { createActorView(it) }
    }

    @GetMapping("/{actorId}")
    fun getActor(@PathVariable actorId: ActorId): Mono<ActorView> {
        return actorService.getActor(actorId).flatMap { createActorView(it) }
    }

    private fun createActorView(actor: Actor): Mono<ActorView> {
        return roleService.getRoleByRoleId(actor.roleId)
            .flatMap { role ->
                policyService.getPolicies((role.policies + actor.policies).toList())
                    .collectList()
                    .map { policies -> ActorView.from(actor, role, policies) }
            }
    }
}

