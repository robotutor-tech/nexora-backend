package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ActionRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ActionResponse
import com.robotutor.nexora.modules.automation.application.ActionUseCase
import com.robotutor.nexora.modules.automation.domain.entity.ActionId
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.ActionMapper
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
@RequestMapping("/actions")
class ActionController(private val actionUseCase: ActionUseCase) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_ACTION)
    @PostMapping
    fun createAction(
        @RequestBody @Validated request: ActionRequest,
        actorData: ActorData
    ): Mono<ActionResponse> {
        val createActionCommand = ActionMapper.toCreateActionCommand(request)
        return actionUseCase.createAction(createActionCommand, actorData)
            .map { ActionMapper.toActionResponse(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.AUTOMATION_ACTION)
    @GetMapping
    fun getActions(actorData: ActorData, data: ResourcesData): Flux<ActionResponse> {
        val actionsIds = data.getResourceIds(ActionType.LIST, ResourceType.AUTOMATION_ACTION).map { ActionId(it) }
        return actionUseCase.getActions(actionsIds, actorData)
            .map { ActionMapper.toActionResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.AUTOMATION_ACTION, "actionId")
    @GetMapping("/{actionId}")
    fun getAction(@PathVariable actionId: String, actorData: ActorData): Mono<ActionResponse> {
        return actionUseCase.getAction(ActionId(actionId), actorData)
            .map { ActionMapper.toActionResponse(it) }
    }
}

