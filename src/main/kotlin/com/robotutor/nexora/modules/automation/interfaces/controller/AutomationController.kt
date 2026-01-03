package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.AutomationService
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/automations")
class AutomationController(private val automationService: AutomationService) {

    @PostMapping
    fun createAutomationRule(
        @RequestBody @Validated request: AutomationRequest,
        actorData: ActorData
    ): Mono<AutomationResponse> {
        val createAutomationCommand = AutomationMapper.toCreateAutomationCommand(request)
        return automationService.createAutomationRule(createAutomationCommand, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @GetMapping
    fun getAutomationRules(actorData: ActorData, resources: Resources): Flux<AutomationResponse> {
        val automationIds = emptyList<AutomationId>()
        return automationService.getAutomationRules(automationIds, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

