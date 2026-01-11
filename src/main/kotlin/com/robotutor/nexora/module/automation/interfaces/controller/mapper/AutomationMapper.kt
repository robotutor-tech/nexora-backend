package com.robotutor.nexora.module.automation.interfaces.controller.mapper

import com.robotutor.nexora.module.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.domain.entity.*
import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.component.Trigger
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.interfaces.controller.dto.CreateAutomationRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object AutomationMapper {
    fun toCreateAutomationCommand(request: CreateAutomationRequest, actorData: ActorData): CreateAutomationCommand {
        return CreateAutomationCommand(
            triggers = toTriggers(request.triggers),
            actions = toActions(request.actions),
            condition = request.condition,
            executionMode = request.executionMode ?: ExecutionMode.MULTIPLE,
            name = Name(request.name),
            description = request.description,
            createdBy = actorData.actorId,
            premisesId = actorData.premisesId
        )
    }

    private fun toTriggers(request: List<String>): Triggers {
        return Triggers(request.map { Trigger() })
    }

    private fun toActions(request: List<String>): Actions {
        return Actions(request.map { Action() })
    }


    fun toAutomationResponse(automation: Automation): AutomationResponse {
        return AutomationResponse(
            automationId = automation.automationId.value,
            premisesId = automation.premisesId.value,
            name = automation.name.value,
            triggers = automation.triggers.ruleIds.map { it.value },
            condition = automation.condition?.let { ConditionNodeMapper.toConditionNodeResponse(automation.condition) },
            actions = automation.actions.ruleIds.map { it.value },
            state = automation.state,
            executionMode = automation.executionMode,
            createdOn = automation.createdOn,
            expiresOn = automation.updatedOn
        )
    }
}
