package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.ActionType
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ActionConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ActionConfigResponse
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ActionRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val type: ActionType,
    val config: ActionConfigRequest,
)

data class ActionResponse(
    val actionId: String,
    val premisesId: String,
    val name: String,
    val description: String?,
    val config: ActionConfigResponse,
    val createdOn: Instant,
    val updatedOn: Instant,
)

