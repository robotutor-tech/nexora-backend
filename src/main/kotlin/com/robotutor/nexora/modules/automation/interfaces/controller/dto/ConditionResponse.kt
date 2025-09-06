package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.ConditionType
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ConditionConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ConditionConfigResponse
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ConditionRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val config: ConditionConfigRequest,
    val type: ConditionType,
)

data class ConditionResponse(
    val conditionId: String,
    val premisesId: String,
    val name: String,
    val description: String?,
    val config: ConditionConfigResponse,
    val createdOn: Instant,
    val updatedOn: Instant,
)