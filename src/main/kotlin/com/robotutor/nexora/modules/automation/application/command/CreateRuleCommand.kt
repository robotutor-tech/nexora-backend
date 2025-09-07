package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.shared.domain.model.Name

data class CreateRuleCommand(
    val name: Name,
    val description: String? = null,
    val type: RuleType,
    val config: Config
)
