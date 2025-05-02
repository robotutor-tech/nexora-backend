package com.robotutor.nexora.orchestration.controllers

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.orchestration.services.OrchestratorService
import com.robotutor.nexora.security.models.InvitationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration")
class OrchestrationController(private val orchestratorService: OrchestratorService) {

    @PostMapping("/users/register")
    fun registerUser(@RequestBody @Validated userRegistrationRequest: UserRegistrationRequest): Mono<UserView> {
        return orchestratorService.registerUser(userRegistrationRequest)
    }

    @PostMapping("/premises/register")
    fun registerPremises(@RequestBody @Validated request: PremisesRegistrationRequest): Mono<PremisesView> {
        return orchestratorService.registerPremises(request)
    }

    @GetMapping("/premises")
    fun registerPremises(): Flux<PremisesView> {
        return orchestratorService.getAllPremises()
    }

    @PostMapping("/devices/register")
    fun registerDevice(@RequestBody @Validated request: DeviceRegistrationRequest, invitationData: InvitationData): Mono<TokenView> {
        return orchestratorService.registerDevice(request, invitationData)
    }
}