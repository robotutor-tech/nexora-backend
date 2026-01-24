package com.robotutor.nexora.module.automation.interfaces.controller.view

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class ConditionNodeRequest(
    @field:NotBlank(message = "Condition kind is required")
    val kind: String,

    val data: Map<String, Any?> = emptyMap(),

    @field:Valid
    val children: List<ConditionNodeRequest> = emptyList(),
)

