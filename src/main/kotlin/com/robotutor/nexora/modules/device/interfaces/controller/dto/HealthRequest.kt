package com.robotutor.nexora.modules.device.interfaces.controller.dto

data class HealthRequest(
    val health: Health,
)

enum class Health {
    CONNECTED, DISCONNECTED,
}