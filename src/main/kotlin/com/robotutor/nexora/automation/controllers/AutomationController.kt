package com.robotutor.nexora.automation.controllers

import com.robotutor.nexora.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.automation.controllers.views.AutomationView
import com.robotutor.nexora.automation.services.AutomationService
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}