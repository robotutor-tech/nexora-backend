package com.robotutor.nexora.module.device.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class RegisterDeviceRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "ZoneId is required")
    val zoneId: String,
)