package com.robotutor.nexora.module.zone.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class WidgetsRequest(
    val feedIds: List<String>,
    @field:NotBlank(message = "Name is required")
    val zoneId: String,
    @field:NotBlank(message = "Name is required")
    val modelNo: String,
)
