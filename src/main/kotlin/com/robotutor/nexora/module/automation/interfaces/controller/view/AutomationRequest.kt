package com.robotutor.nexora.module.automation.interfaces.controller.view

import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.request.ComponentRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class AutomationRequest(
    @field:NotEmpty(message = "At least one trigger is required")
    @field:Valid
    val triggers: List<ComponentRequest>,

    @field:NotEmpty(message = "At least one action is required")
    @field:Valid
    val actions: List<ComponentRequest>,

    @field:Valid
    val condition: ConditionNodeRequest? = null,

    val executionMode: ExecutionMode? = null,

    @field:NotBlank(message = "Name is required")
    val name: String,

    val description: String? = null,
)

