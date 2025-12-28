package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.AutomationUseCase
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.common.security.interfaces.view.AuthorizedResources
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
        actorData: ActorData
    ): Mono<AutomationResponse> {
        val createAutomationCommand = AutomationMapper.toCreateAutomationCommand(request)
        return automationUseCase.createAutomationRule(createAutomationCommand, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @HttpAuthorize(ActionType.READ, ResourceType.AUTOMATION)
    @GetMapping
    fun getAutomationRules(actorData: ActorData, authorizedResources: AuthorizedResources): Flux<AutomationResponse> {
        val automationIds = emptyList<AutomationId>()
        return automationUseCase.getAutomationRules(automationIds, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

