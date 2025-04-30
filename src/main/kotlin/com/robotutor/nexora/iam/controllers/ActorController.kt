package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/iam/actors")
class ActorController(private val actorService: ActorService, private val roleService: RoleService) {

    @PostMapping
    fun registerActor(@RequestBody @Validated request: RegisterActorRequest): Flux<ActorView> {
        return actorService.registerActor(request)
            .flatMap { actor -> roleService.getRoleByRoleId(actor.roleId).map { ActorView.from(actor, it) } }
    }

    @GetMapping
    fun getActors(authUserData: AuthUserData): Flux<ActorView> {
        return actorService.getActors(authUserData)
            .flatMap { actor -> roleService.getRoleByRoleId(actor.roleId).map { ActorView.from(actor, it) } }
    }

//    @GetMapping("/{actorId}")
//    fun getPremisesActor(@PathVariable actorId: ActorId): Mono<ActorView> {
//        return actorService.getActor(actorId).map { ActorView.from(it) }
//    }
}