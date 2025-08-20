package com.robotutor.nexora.modules.automation.controllers

import com.robotutor.nexora.modules.automation.controllers.views.ConditionRequest
import com.robotutor.nexora.modules.automation.controllers.views.ConditionView
import com.robotutor.nexora.modules.automation.services.ConditionService
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
@RequestMapping("/conditions")
class ConditionController(private val conditionService: ConditionService) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_CONDITION)
    @PostMapping
    fun createCondition(
        @RequestBody @Validated request: ConditionRequest,
        premisesActorData: PremisesActorData
    ): Mono<ConditionView> {
        return conditionService.createCondition(request, premisesActorData)
            .map { ConditionView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_CONDITION)
    @GetMapping
    fun getConditions(premisesActorData: PremisesActorData, data: ResourcesData): Flux<ConditionView> {
        val conditionIds = data.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_CONDITION)
        return conditionService.getConditions(conditionIds, premisesActorData)
            .map { ConditionView.from(it) }
    }
}