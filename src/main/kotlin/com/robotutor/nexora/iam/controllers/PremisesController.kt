package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.controllers.view.PremisesRequest
import com.robotutor.nexora.iam.models.ActorId
import com.robotutor.nexora.iam.services.ActorService
import com.robotutor.nexora.iam.services.PremisesService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController(value = "IAMPremisesController")
@RequestMapping("/iam/premises")
class PremisesController(
    private val premisesService: PremisesService,
    private val actorService: ActorService,
    private val roleService: RoleService
) {

    @PostMapping("/register")
    fun registerPremises(
        @RequestBody @Validated request: PremisesRequest,
        authUserData: AuthUserData
    ): Flux<ActorView> {
        return premisesService.registerPremises(request, authUserData)
            .flatMap { actor -> roleService.getRoleByRoleId(actor.roleId).map { ActorView.from(actor, it) } }

    }

    @GetMapping("/{actorId}")
    fun getPremisesActor(@PathVariable actorId: ActorId): Mono<ActorView> {
        return actorService.getActor(actorId)
            .flatMap { actor -> roleService.getRoleByRoleId(actor.roleId).map { ActorView.from(actor, it) } }
    }
}