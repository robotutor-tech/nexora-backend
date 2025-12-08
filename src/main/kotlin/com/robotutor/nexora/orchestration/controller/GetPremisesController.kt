package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import com.robotutor.nexora.orchestration.workflow.GetPremisesWorkflow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/orchestration")
class GetPremisesController(
    private val getPremisesWorkflow: GetPremisesWorkflow,
) {
    @GetMapping("/premises")
    fun registerUser(accountData: AccountData): Flux<PremisesResponse> {
        return getPremisesWorkflow.getPremises(accountData)
    }
}