package com.robotutor.nexora.context.device.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class ActivateDeviceRequest(
    @field:NotBlank(message = "Name is required")
    val osName: String,
    @field:NotBlank(message = "Name is required")
    val osVersion: String,
    @field:NotBlank(message = "Name is required")
    val modelNo: String,
    @field:NotBlank(message = "Name is required")
    val serialNo: String,
)