package com.robotutor.nexora.context.iam.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class AuthDeviceRegisterRequest(
    @field:NotBlank(message = "DeviceId is required")
    val deviceId: String,
    @field:NotBlank(message = "ActorId is required")
    val actorId: String,
    @field:NotBlank(message = "RoleId is required")
    val roleId: String
)

