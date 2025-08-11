package com.robotutor.nexora.modules.iam.controllers

import com.robotutor.nexora.modules.iam.controllers.view.ActorWithRolesView
import com.robotutor.nexora.modules.iam.controllers.view.ActorView
import com.robotutor.nexora.modules.iam.controllers.view.PremisesRequest
import com.robotutor.nexora.modules.iam.controllers.view.RegisterDeviceRequest
import com.robotutor.nexora.modules.iam.services.PremisesService
import com.robotutor.nexora.modules.iam.services.RoleService
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.common.security.models.InvitationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController(value = "IAMPremisesController")
@RequestMapping("/iam/premises")
class PremisesController(
    private val premisesService: PremisesService,
    private val roleService: RoleService,
) {

    @PostMapping("/register")
    fun registerPremises(
        @RequestBody @Validated request: PremisesRequest,
        authUserData: AuthUserData
    ): Mono<ActorWithRolesView> {
        return premisesService.registerPremises(request, authUserData)
            .flatMap { actor ->
                roleService.getRolesByRoleIds(actor.roles.toList())
                    .collectList()
                    .map { ActorWithRolesView.from(actor, it) }
            }
    }

    @PostMapping("/register/device")
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        invitationData: InvitationData
    ): Mono<ActorView> {
        return premisesService.registerDevice(request, invitationData)
            .flatMap { actor ->
                roleService.getRoleByRoleId(actor.roles.first())
                    .map { ActorView.from(actor, it) }
            }
    }
}