package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.TriggerType
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TriggerConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TriggerConfigResponse
import jakarta.validation.constraints.NotBlank
import java.time.DayOfWeek
import java.time.Instant

data class TriggerRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val config: TriggerConfigRequest,
    val type: TriggerType,
)

data class ScheduleTriggerRequest(
    val type: TriggerType = TriggerType.SCHEDULE,
    val config: Map<String, Any?>,
    val repeat: List<DayOfWeek>
)

data class TriggerResponse(
    val triggerId: String,
    val premisesId: String,
    val name: String,
    val description: String?,
    val config: TriggerConfigResponse,
    val createdOn: Instant,
    val updatedOn: Instant,
)
