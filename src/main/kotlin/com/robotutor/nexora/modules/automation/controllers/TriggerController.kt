package com.robotutor.nexora.modules.automation.controllers

import com.robotutor.nexora.modules.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.modules.automation.controllers.views.TriggerView
import com.robotutor.nexora.modules.automation.models.TriggerId
import com.robotutor.nexora.modules.automation.services.TriggerService
import com.robotutor.nexora.common.security.filters.annotations.ActionType
import com.robotutor.nexora.common.security.filters.annotations.RequireAccess
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/triggers")
class TriggerController(private val triggerService: TriggerService) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_TRIGGER)
    @PostMapping
    fun createTrigger(
        @RequestBody @Validated request: TriggerRequest,
        premisesActorData: PremisesActorData
    ): Mono<TriggerView> {
        return triggerService.createTrigger(request, premisesActorData)
            .map { TriggerView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_TRIGGER)
    @GetMapping
    fun getTriggers(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<TriggerView> {
        val triggerIds = resourcesData.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_TRIGGER)
        return triggerService.getAllTriggers(triggerIds, premisesActorData)
            .map { TriggerView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.AUTOMATION_TRIGGER, "triggerId")
    @GetMapping("/{triggerId}")
    fun getTrigger(@PathVariable triggerId: TriggerId, premisesActorData: PremisesActorData): Mono<TriggerView> {
        return triggerService.getTrigger(triggerId, premisesActorData)
            .map { TriggerView.from(it) }
    }
}