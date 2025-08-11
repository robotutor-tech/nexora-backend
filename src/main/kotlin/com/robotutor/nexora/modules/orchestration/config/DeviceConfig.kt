package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.device-service")
data class DeviceConfig(
    val baseUrl: String,
    val register: String = "/devices/register",
)