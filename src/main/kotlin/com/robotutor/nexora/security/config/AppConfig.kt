package com.robotutor.nexora.security.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app.security")
data class AppConfig(
    val authServiceBaseUrl: String,
    val internalAccessToken: String,
    val unSecuredPath: List<String> = emptyList(),
    val partiallySecuredPath: List<String> = emptyList(),
    val validatePath: String = "/auth/tokens/validate"
)
