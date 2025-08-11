package com.robotutor.nexora.modules.orchestration.controllers

import com.robotutor.nexora.modules.auth.controllers.views.TokenView
import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.modules.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.modules.orchestration.gateway.view.PremisesWithActorView
import com.robotutor.nexora.modules.orchestration.services.OrchestratorService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration")
class OrchestrationController(private val orchestratorService: OrchestratorService) {

//    @PostMapping("/users/register")
//    fun registerUser(@RequestBody @Validated userRegistrationRequest: UserRegistrationRequest): Mono<UserView> {
//        return orchestratorService.registerUser(userRegistrationRequest)
//    }
//
//    @PostMapping("/premises/register")
//    fun registerPremises(@RequestBody @Validated request: PremisesRegistrationRequest): Mono<PremisesWithActorView> {
//        return orchestratorService.registerPremises(request)
//    }

    @GetMapping("/premises")
    fun getAllPremises(): Flux<PremisesWithActorView> {
        return orchestratorService.getAllPremises()
    }

    @PostMapping("/devices/register")
    fun registerDevice(
        @RequestBody @Validated request: DeviceRegistrationRequest,
        invitationData: InvitationData
    ): Mono<TokenView> {
        return orchestratorService.registerDevice(request, invitationData)
    }
}