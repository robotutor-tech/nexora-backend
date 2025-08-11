package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.auth-service")
data class AuthConfig(
    val baseUrl: String,
    val register: String = "/auth/register",
    val validateInvitation: String = "/auth/invitations/validate",
    val deviceToken: String = "/auth/tokens/device",
)