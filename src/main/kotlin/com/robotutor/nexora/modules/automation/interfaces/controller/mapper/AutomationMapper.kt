package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.modules.automation.domain.entity.*
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.shared.domain.model.Name

object AutomationMapper {
    fun toCreateAutomationCommand(request: AutomationRequest): CreateAutomationCommand {
        return CreateAutomationCommand(
            triggers = Triggers(request.triggers.map { TriggerId(it) }),
            actions = Actions(request.actions.map { ActionId(it) }),
            condition = request.condition,
            executionMode = request.executionMode ?: ExecutionMode.MULTIPLE,
            name = Name(request.name),
            description = request.description
        )
    }


    fun toAutomationResponse(automation: Automation): AutomationResponse {
        return AutomationResponse(
            automationId = automation.automationId.value,
            premisesId = automation.premisesId.value,
            name = automation.name.value,
            triggers = automation.triggers.triggerIds.map { it.value },
            condition = automation.condition?.let { ConditionNodeMapper.toConditionNodeResponse(automation.condition) },
            actions = automation.actions.actionIds.map { it.value },
            state = automation.state,
            executionMode = automation.executionMode,
            createdOn = automation.createdOn,
            expiresOn = automation.updatedOn
        )
    }
}
