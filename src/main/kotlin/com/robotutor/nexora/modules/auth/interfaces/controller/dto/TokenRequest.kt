package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import jakarta.validation.constraints.NotBlank

data class DeviceTokenRequest(
    @field:NotBlank(message = "Device Id is required")
    val deviceId: String
)