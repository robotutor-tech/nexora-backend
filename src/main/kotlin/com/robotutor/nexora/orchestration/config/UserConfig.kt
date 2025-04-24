package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.user-service")
data class UserConfig(val baseUrl: String, val register: String = "/users/register")