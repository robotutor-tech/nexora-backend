package com.robotutor.nexora.device.controllers.view

import jakarta.validation.constraints.NotBlank

data class DeviceRequest(
    @field:NotBlank(message = "Model No is required")
    val modelNo: String,
)