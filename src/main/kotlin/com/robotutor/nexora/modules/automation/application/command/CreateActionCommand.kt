package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.ActionType
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.shared.domain.model.Name

data class CreateActionCommand(
    val name: Name,
    val description: String? = null,
    val type: ActionType,
    val config: ActionConfig
)
