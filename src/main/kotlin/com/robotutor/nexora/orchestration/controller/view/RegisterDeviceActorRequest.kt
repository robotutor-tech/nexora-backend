package com.robotutor.nexora.orchestration.controller.view

import jakarta.validation.constraints.NotBlank

data class RegisterDeviceActorRequest(
    @field:NotBlank(message = "ModelNo is required")
    val modelNo: String,
    @field:NotBlank(message = "SerialNo is required")
    val serialNo: String,
    @field:NotBlank(message = "OsName is required")
    val osName: String,
    @field:NotBlank(message = "OsVersion is required")
    val osVersion: String,
)

