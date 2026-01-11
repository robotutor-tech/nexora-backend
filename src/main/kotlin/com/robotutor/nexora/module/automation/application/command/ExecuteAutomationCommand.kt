package com.robotutor.nexora.module.automation.application.command

import com.robotutor.nexora.module.automation.domain.vo.AutomationId

data class ExecuteAutomationCommand(
    val automationId: AutomationId
)
