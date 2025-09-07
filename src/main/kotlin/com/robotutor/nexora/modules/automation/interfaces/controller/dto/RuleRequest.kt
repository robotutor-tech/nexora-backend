package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.ConfigRequest
import jakarta.validation.constraints.NotBlank

data class RuleRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val type: RuleType,
    val description: String? = null,
    val config: ConfigRequest,
)
