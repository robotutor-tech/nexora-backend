package com.robotutor.nexora.modules.orchestration.controllers

import com.robotutor.nexora.modules.orchestration.services.OrchestratorService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orchestration")
class OrchestrationController(private val orchestratorService: OrchestratorService) {

//    @PostMapping("/devices/register")
//    fun registerDevice(
//        @RequestBody @Validated request: DeviceRegistrationRequest,
//        invitationData: InvitationData
//    ): Mono<TokenView> {
//        return orchestratorService.registerDevice(request, invitationData)
//    }
}