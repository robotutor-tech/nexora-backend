package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorWithRoleView
import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.PolicyService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.orchestration.gateway.view.PremisesActorView
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(
    private val actorService: ActorService,
    private val roleService: RoleService,
    private val policyService: PolicyService
) {
    @GetMapping
    fun getActors(authUserData: AuthUserData): Flux<ActorView> {
        return actorService.getActors(authUserData).flatMap { createActorView(it) }
    }

    @GetMapping("/{actorId}")
    fun getActor(@PathVariable actorId: ActorId, @RequestParam roleId: RoleId = ""): Mono<ActorWithRoleView> {
        return actorService.getActor(actorId, roleId)
            .flatMap { actor ->
                roleService.getRoleByRoleId(roleId)
                    .flatMap { role ->
                        policyService.getPolicies(role.policies.toList())
                            .collectList()
                            .map { ActorWithRoleView.from(actor, role, it) }
                    }
            }
    }

    private fun createActorView(actor: Actor): Mono<ActorView> {
        return roleService.getRolesByRoleIds(actor.roles.toList())
            .collectList()
            .map { roles -> ActorView.from(actor, roles) }
    }
}

