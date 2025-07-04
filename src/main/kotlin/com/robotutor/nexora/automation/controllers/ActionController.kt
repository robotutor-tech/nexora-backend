package com.robotutor.nexora.automation.controllers

import com.robotutor.nexora.automation.controllers.views.ActionRequest
import com.robotutor.nexora.automation.controllers.views.ActionView
import com.robotutor.nexora.automation.services.ActionService
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
@RequestMapping("/actions")
class ActionController(private val actionService: ActionService) {

    @RequireAccess(ActionType.CREATE, ResourceType.AUTOMATION_ACTION)
    @PostMapping
    fun createAction(
        @RequestBody @Validated request: ActionRequest,
        premisesActorData: PremisesActorData
    ): Mono<ActionView> {
        return actionService.createAction(request, premisesActorData)
            .map { ActionView.from(it) }
    }
}