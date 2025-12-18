package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.AutomationUseCase
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/automations")
class AutomationController(private val automationUseCase: AutomationUseCase) {

    @Authorize(ActionType.WRITE, ResourceType.AUTOMATION)
    @PostMapping
    fun createAutomationRule(
        @RequestBody @Validated request: AutomationRequest,
        actorData: ActorData
    ): Mono<AutomationResponse> {
        val createAutomationCommand = AutomationMapper.toCreateAutomationCommand(request)
        return automationUseCase.createAutomationRule(createAutomationCommand, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.AUTOMATION)
    @GetMapping
    fun getAutomationRules(actorData: ActorData, data: ResourcesData): Flux<AutomationResponse> {
        val automationIds = data.getResourceIds(ActionType.READ, ResourceType.AUTOMATION)
            .map { AutomationId(it) }
        return automationUseCase.getAutomationRules(automationIds, actorData)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

