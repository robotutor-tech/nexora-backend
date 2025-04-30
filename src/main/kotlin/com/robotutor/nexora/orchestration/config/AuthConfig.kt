package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.auth-service")
data class AuthConfig(
    val baseUrl: String,
    val register: String = "/auth/register",
    val validateInvitation: String = "/auth/validate/invitation",
)