package com.robotutor.nexora.modules.automation.application.command

import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.shared.domain.model.Name

data class CreateTriggerCommand(
    val name: Name,
    val description: String? = null,
    val config: TriggerConfig
)
