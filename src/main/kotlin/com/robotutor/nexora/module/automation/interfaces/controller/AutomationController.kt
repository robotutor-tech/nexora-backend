package com.robotutor.nexora.module.automation.interfaces.controller

import com.robotutor.nexora.module.automation.application.service.CreateAutomationService
import com.robotutor.nexora.module.automation.domain.entity.AutomationId
import com.robotutor.nexora.module.automation.interfaces.controller.dto.CreateAutomationRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.module.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/automations")
class AutomationController(private val createAutomationService: CreateAutomationService) {

    @PostMapping
    fun createAutomationRule(
        @RequestBody @Validated request: CreateAutomationRequest,
        actorData: ActorData
    ): Mono<AutomationResponse> {
        val command = AutomationMapper.toCreateAutomationCommand(request, actorData)
        return createAutomationService.execute(command)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @GetMapping
    fun getAutomationRules(actorData: ActorData, resources: Resources): Flux<AutomationResponse> {
        val automationIds = emptyList<AutomationId>()
        return automationService.getAutomationRules(automationIds, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

