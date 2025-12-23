package com.robotutor.nexora.context.device.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class DeviceMetaDataRequest(
    @field:NotBlank(message = "OS name is required")
    val osName: String,
    @field:NotBlank(message = "OS version is required")
    val osVersion: String,
    @field:NotBlank(message = "Model no is required")
    val modelNo: String,
    @field:NotBlank(message = "Serial no is required")
    val serialNo: String,
)