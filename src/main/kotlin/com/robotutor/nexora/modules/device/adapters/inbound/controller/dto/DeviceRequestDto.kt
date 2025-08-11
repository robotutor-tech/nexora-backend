package com.robotutor.nexora.modules.device.adapters.inbound.controller.dto

import com.robotutor.nexora.modules.device.domain.model.DeviceType
import jakarta.validation.constraints.NotBlank

data class DeviceRequest(
    @field:NotBlank(message = "Model No is required")
    val modelNo: String,
    @field:NotBlank(message = "Serial no is required")
    val serialNo: String,
    val deviceType: DeviceType,
)