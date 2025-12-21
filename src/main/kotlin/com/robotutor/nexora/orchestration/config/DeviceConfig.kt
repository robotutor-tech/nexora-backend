package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.device")
data class DeviceConfig(
    val baseUrl: String = "",
    val path: String = "/devices",
    val metadataPath: String = "/devices/metadata"
)