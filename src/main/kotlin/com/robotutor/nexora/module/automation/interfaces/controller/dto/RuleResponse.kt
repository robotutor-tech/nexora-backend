package com.robotutor.nexora.module.automation.interfaces.controller.dto

import com.robotutor.nexora.module.automation.domain.entity.RuleType
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.ConfigResponse
import java.time.Instant


data class RuleResponse(
    val ruleId: String,
    val premisesId: String,
    val name: String,
    val type: RuleType,
    val description: String?,
    val config: ConfigResponse,
    val createdOn: Instant,
    val updatedOn: Instant,
)
