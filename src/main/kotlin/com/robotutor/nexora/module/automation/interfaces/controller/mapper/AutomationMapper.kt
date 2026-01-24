package com.robotutor.nexora.module.automation.interfaces.controller.mapper

import com.robotutor.nexora.module.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.domain.vo.component.*
import com.robotutor.nexora.module.automation.interfaces.controller.view.AutomationRequest
import com.robotutor.nexora.module.automation.interfaces.controller.view.AutomationResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.request.*
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.AutomationComponentResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.ComponentResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.FeedControlResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.FeedValueResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.VoiceResponse
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.WaitResponse
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import java.util.concurrent.TimeUnit

object AutomationMapper {
    fun toCreateAutomationCommand(request: AutomationRequest, actorData: ActorData): CreateAutomationCommand {
        return CreateAutomationCommand(
            triggers = toTriggers(request.triggers),
            actions = toActions(request.actions),
            condition = null,
            executionMode = request.executionMode ?: ExecutionMode.MULTIPLE,
            name = Name(request.name),
            description = request.description,
            createdBy = actorData.actorId,
            premisesId = actorData.premisesId
        )
    }

    fun toAutomationResponse(automation: AutomationAggregate): AutomationResponse {
        return AutomationResponse(
            automationId = automation.automationId.value,
            premisesId = automation.premisesId.value,
            name = automation.name.value,
            triggers = toTriggersResponse(automation.triggers),
            condition = null,
            actions = toActionsResponse(automation.actions),
            state = automation.state,
            executionMode = automation.executionMode,
            createdOn = automation.createdOn,
            expiresOn = automation.updatedOn
        )
    }

    private fun toTriggers(request: List<ComponentRequest>): Triggers {
        val triggers = request.map { component -> toComponent(component) as Trigger }
        return Triggers(triggers)
    }


    private fun toActions(request: List<ComponentRequest>): Actions {
        val actions = request.map { component -> toComponent(component) as Action }
        return Actions(actions)
    }


    private fun toComponent(component: ComponentRequest): Component {
        return when (component) {
            is AutomationComponentRequest -> AutomationComponent(AutomationId(component.automationId))
            is FeedControlRequest -> FeedControl(FeedId(component.feedId), component.operator, component.value)
            is FeedValueRequest -> FeedValue(FeedId(component.feedId), component.value)
            is VoiceRequest -> Voice(component.commands)
            is WaitRequest -> Wait(component.duration.toLong(), TimeUnit.SECONDS)
        }
    }

    private fun toTriggersResponse(triggers: Triggers): List<ComponentResponse> {
        return triggers.values.map { component -> toComponentResponse(component) }
    }


    private fun toActionsResponse(actions: Actions): List<ComponentResponse> {
        return actions.values.map { component -> toComponentResponse(component) }
    }


    private fun toComponentResponse(component: Component): ComponentResponse {
        return when (component.type) {
            ComponentType.AUTOMATION -> {
                component as AutomationComponent
                AutomationComponentResponse(component.automationId.value)
            }

            ComponentType.FEED_CONTROL -> {
                component as FeedControl
                FeedControlResponse(component.feedId.value, component.value, component.operator)
            }

            ComponentType.FEED_VALUE -> {
                component as FeedValue
                FeedValueResponse(component.feedId.value, component.value)
            }

            ComponentType.VOICE -> {
                component as Voice
                VoiceResponse(component.commands)
            }

            ComponentType.WAIT -> {
                component as Wait
                WaitResponse(component.toSeconds().duration)
            }
        }
    }
}
