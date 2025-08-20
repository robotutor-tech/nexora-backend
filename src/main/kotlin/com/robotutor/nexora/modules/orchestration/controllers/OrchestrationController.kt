package com.robotutor.nexora.modules.orchestration.controllers

import com.robotutor.nexora.modules.orchestration.gateway.view.PremisesWithActorView
import com.robotutor.nexora.modules.orchestration.services.OrchestratorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

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

//    @PostMapping("/devices/register")
//    fun registerDevice(
//        @RequestBody @Validated request: DeviceRegistrationRequest,
//        invitationData: InvitationData
//    ): Mono<TokenView> {
//        return orchestratorService.registerDevice(request, invitationData)
//    }
}