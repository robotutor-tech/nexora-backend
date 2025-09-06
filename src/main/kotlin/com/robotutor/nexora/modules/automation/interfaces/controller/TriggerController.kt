package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.TriggerUseCase
import com.robotutor.nexora.modules.automation.domain.entity.TriggerId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.TriggerRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.TriggerResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.TriggerMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
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
class TriggerController(private val triggerUseCase: TriggerUseCase) {

//    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_TRIGGER)
    @PostMapping
    fun createTrigger(@RequestBody @Validated request: TriggerRequest, actorData: ActorData): Mono<TriggerResponse> {
        println("$request---------------------------")
        val command = TriggerMapper.toCreateTriggerCommand(request)
        return triggerUseCase.createTrigger(command, actorData)
            .map { TriggerMapper.toTriggerResponse(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_TRIGGER)
    @GetMapping
    fun getTriggers(actorData: ActorData, resourcesData: ResourcesData): Flux<TriggerResponse> {
        val triggerIds = resourcesData.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_TRIGGER)
            .map { TriggerId(it) }
        return triggerUseCase.getTriggers(triggerIds, actorData)
            .map { TriggerMapper.toTriggerResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.AUTOMATION_TRIGGER, "triggerId")
    @GetMapping("/{triggerId}")
    fun getTrigger(@PathVariable triggerId: String, actorData: ActorData): Mono<TriggerResponse> {
        return triggerUseCase.getTrigger(TriggerId(triggerId), actorData)
            .map { TriggerMapper.toTriggerResponse(it) }
    }
}

