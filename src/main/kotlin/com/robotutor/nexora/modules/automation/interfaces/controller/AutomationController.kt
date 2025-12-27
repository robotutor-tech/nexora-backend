package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.AutomationUseCase
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.shared.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/automations")
class AutomationController(private val automationUseCase: AutomationUseCase) {

    @HttpAuthorize(ActionType.UPDATE, ResourceType.AUTOMATION)
    @PostMapping
    fun createAutomationRule(
        @RequestBody @Validated request: AutomationRequest,
        ActorData: ActorData
    ): Mono<AutomationResponse> {
        val createAutomationCommand = AutomationMapper.toCreateAutomationCommand(request)
        return automationUseCase.createAutomationRule(createAutomationCommand, ActorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @HttpAuthorize(ActionType.READ, ResourceType.AUTOMATION)
    @GetMapping
    fun getAutomationRules(ActorData: ActorData, authorizedResources: AuthorizedResources): Flux<AutomationResponse> {
        val automationIds = emptyList<AutomationId>()
        return automationUseCase.getAutomationRules(automationIds, ActorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

