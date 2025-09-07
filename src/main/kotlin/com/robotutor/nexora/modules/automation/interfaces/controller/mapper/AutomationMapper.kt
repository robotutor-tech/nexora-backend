package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.modules.automation.domain.entity.*
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.AutomationResponse
import com.robotutor.nexora.shared.domain.model.Name

object AutomationMapper {
    fun toCreateAutomationCommand(request: AutomationRequest): CreateAutomationCommand {
        return CreateAutomationCommand(
            triggers = Rules(request.triggers.map { RuleId(it) }),
            actions = Rules(request.actions.map { RuleId(it) }),
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
