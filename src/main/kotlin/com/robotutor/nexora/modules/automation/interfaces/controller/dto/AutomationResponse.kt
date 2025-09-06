package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.AutomationState
import com.robotutor.nexora.modules.automation.domain.entity.ExecutionMode
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.time.Instant

data class AutomationRequest(
    @field:NotEmpty(message = "At least one trigger is required")
    val triggers: List<String>,
    @field:NotEmpty(message = "At least one action is required")
    val actions: List<String>,
    val condition: ConditionNode? = null,
    val executionMode: ExecutionMode? = null,
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
)

data class AutomationResponse(
    val automationId: String,
    val premisesId: String,
    val name: String,
    val triggers: List<String>,
    val condition: ConditionNodeResponse?,
    val actions: List<String>,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
)



