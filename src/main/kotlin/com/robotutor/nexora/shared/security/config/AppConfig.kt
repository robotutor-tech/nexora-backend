package com.robotutor.nexora.shared.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.security")
data class AppConfig(
    val refreshPath: String = "/refresh",
    val unSecuredPath: List<String> = emptyList(),
)
