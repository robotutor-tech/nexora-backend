package com.robotutor.nexora.module.identity.infrastructure.facade

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.identity")
data class IdentityConfig(
    val userBaseUrl: String,
    val registerUserPath: String = "/users/register",
)
