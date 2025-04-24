package com.robotutor.nexora.orchestration.controllers

import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.orchestration.services.OrchestratorService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}