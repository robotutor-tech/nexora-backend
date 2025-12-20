package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import com.robotutor.nexora.orchestration.controller.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.workflow.PremisesRegistrationWorkflow
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration")
class PremisesRegistrationController(
    private val premisesRegistrationWorkflow: PremisesRegistrationWorkflow,
) {
    @PostMapping("/premises/register")
    fun registerPremises(@RequestBody @Validated premises: PremisesRegistrationRequest): Mono<PremisesResponse> {
        return premisesRegistrationWorkflow.registerPremises(premises)
    }
}