package com.robotutor.nexora.modules.automation.controllers

import com.robotutor.nexora.modules.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.modules.automation.controllers.views.AutomationView
import com.robotutor.nexora.modules.automation.services.AutomationService
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.RequireAccess
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.ResourcesData
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
class AutomationController(private val automationService: AutomationService) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_RULE)
    @PostMapping
    fun createAutomationRule(
        @RequestBody @Validated request: AutomationRequest,
        premisesActorData: PremisesActorData
    ): Mono<AutomationView> {
        return automationService.createAutomationRule(request, premisesActorData)
            .map { AutomationView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_RULE)
    @GetMapping
    fun getAutomationRules(premisesActorData: PremisesActorData, data: ResourcesData): Flux<AutomationView> {
        val automationIds = data.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_RULE)
        return automationService.getAutomationRules(automationIds, premisesActorData)
            .map { AutomationView.from(it) }
    }
}