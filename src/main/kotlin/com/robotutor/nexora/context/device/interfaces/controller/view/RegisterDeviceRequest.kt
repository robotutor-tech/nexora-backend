package com.robotutor.nexora.context.device.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class RegisterDeviceRequest(
    @field:NotBlank(message = "AccountId is required")
    val accountId: String,
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "ZoneId is required")
    val zoneId: String,
)