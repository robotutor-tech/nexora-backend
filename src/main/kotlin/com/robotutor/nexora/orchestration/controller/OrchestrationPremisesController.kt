package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import com.robotutor.nexora.orchestration.controller.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.workflow.GetPremisesWorkflow
import com.robotutor.nexora.orchestration.workflow.PremisesRegistrationWorkflow
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration/premises")
class OrchestrationPremisesController(
    private val getPremisesWorkflow: GetPremisesWorkflow,
    private val premisesRegistrationWorkflow: PremisesRegistrationWorkflow,
) {
    @GetMapping
    fun registerUser(accountData: AccountData): Flux<PremisesResponse> {
        return getPremisesWorkflow.getPremises(accountData)
    }

    @PostMapping("/register")
    fun registerPremises(@RequestBody @Validated premises: PremisesRegistrationRequest): Mono<PremisesResponse> {
        return premisesRegistrationWorkflow.registerPremises(premises)
    }
}