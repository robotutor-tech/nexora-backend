package com.robotutor.nexora.modules.auth.interfaces.controller.dto

data class AuthDeviceResponse(
    val deviceId: String,
    val deviceSecret: String,
)