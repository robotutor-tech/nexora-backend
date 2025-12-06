package com.robotutor.nexora.context.iam.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class DeviceTokenRequest(
    @field:NotBlank(message = "Device Id is required")
    val deviceId: String
)