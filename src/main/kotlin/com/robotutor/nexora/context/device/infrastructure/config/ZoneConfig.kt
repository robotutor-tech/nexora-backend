package com.robotutor.nexora.context.device.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.zone")
data class ZoneConfig(
    val baseUrl: String,
    val widgetsPath: String = "/zones/widgets"
)