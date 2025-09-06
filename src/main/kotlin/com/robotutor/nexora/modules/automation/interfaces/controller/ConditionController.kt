package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.ConditionUseCase
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionResponse
import com.robotutor.nexora.modules.automation.domain.entity.ConditionId
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.ConditionMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
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
@RequestMapping("/conditions")
class ConditionController(private val conditionUseCase: ConditionUseCase) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_CONDITION)
    @PostMapping
    fun createCondition(
        @RequestBody @Validated request: ConditionRequest,
        actorData: ActorData
    ): Mono<ConditionResponse> {
        val createConditionCommand = ConditionMapper.toCreateConditionCommand(request)
        return conditionUseCase.createCondition(createConditionCommand, actorData)
            .map { ConditionMapper.toConditionResponse(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_CONDITION)
    @GetMapping
    fun getConditions(actorData: ActorData, data: ResourcesData): Flux<ConditionResponse> {
        val conditionIds = data.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_CONDITION)
            .map { ConditionId(it) }
        return conditionUseCase.getConditions(conditionIds, actorData)
            .map { ConditionMapper.toConditionResponse(it) }
    }
}

