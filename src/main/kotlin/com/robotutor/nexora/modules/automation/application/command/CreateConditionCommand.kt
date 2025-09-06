package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.config.ConditionConfig
import com.robotutor.nexora.shared.domain.model.Name

data class CreateConditionCommand(
    val name: Name,
    val description: String? = null,
    val config: ConditionConfig,
)
