package com.robotutor.nexora.module.automation.interfaces.controller.dto

import com.robotutor.nexora.module.automation.domain.entity.RuleType
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.ConfigRequest
import jakarta.validation.constraints.NotBlank

data class RuleRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val type: RuleType,
    val description: String? = null,
    val config: ConfigRequest,
)
