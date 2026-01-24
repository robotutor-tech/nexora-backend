package com.robotutor.nexora.module.automation.interfaces.controller

import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.module.automation.application.command.GetAutomationsQuery
import com.robotutor.nexora.module.automation.application.service.CreateAutomationService
import com.robotutor.nexora.module.automation.application.service.GetAutomationService
import com.robotutor.nexora.module.automation.interfaces.controller.mapper.AutomationMapper
import com.robotutor.nexora.module.automation.interfaces.controller.view.AutomationRequest
import com.robotutor.nexora.module.automation.interfaces.controller.view.AutomationResponse
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/automations")
class AutomationController(
    private val createAutomationService: CreateAutomationService,
    private val getAutomationService: GetAutomationService
) {

    @PostMapping
    fun createAutomation(
        @RequestBody @Validated request: AutomationRequest,
        actorData: ActorData
    ): Mono<AutomationResponse> {
        val command = AutomationMapper.toCreateAutomationCommand(request, actorData)
        return createAutomationService.execute(command)
            .map { AutomationMapper.toAutomationResponse(it) }
    }

    @GetMapping
    fun getAllAutomations(
        actorData: ActorData,
        @ResourceSelector(ActionType.READ, ResourceType.AUTOMATION) resources: Resources
    ): Flux<AutomationResponse> {
        val query = GetAutomationsQuery(resources, actorData.actorId)
        return getAutomationService.execute(query)
            .map { AutomationMapper.toAutomationResponse(it) }
    }
}

