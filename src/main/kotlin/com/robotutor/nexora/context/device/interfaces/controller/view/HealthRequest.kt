package com.robotutor.nexora.context.device.interfaces.controller.view

data class HealthRequest(
    val health: Health,
)

enum class Health {
    CONNECTED, DISCONNECTED,
}