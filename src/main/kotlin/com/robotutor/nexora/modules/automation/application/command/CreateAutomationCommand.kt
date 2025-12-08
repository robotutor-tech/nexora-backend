package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.ExecutionMode
import com.robotutor.nexora.modules.automation.domain.entity.Rules
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import com.robotutor.nexora.shared.domain.vo.Name

data class CreateAutomationCommand(
    val triggers: Rules,
    val actions: Rules,
    val condition: ConditionNode?,
    val executionMode: ExecutionMode,
    val name: Name,
    val description: String?,
)
