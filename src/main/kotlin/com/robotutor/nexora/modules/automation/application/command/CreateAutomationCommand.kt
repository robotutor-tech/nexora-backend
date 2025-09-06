package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.Actions
import com.robotutor.nexora.modules.automation.domain.entity.ExecutionMode
import com.robotutor.nexora.modules.automation.domain.entity.Triggers
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import com.robotutor.nexora.shared.domain.model.Name

data class CreateAutomationCommand(
    val triggers: Triggers,
    val actions: Actions,
    val condition: ConditionNode?,
    val executionMode: ExecutionMode,
    val name: Name,
    val description: String?,
)
