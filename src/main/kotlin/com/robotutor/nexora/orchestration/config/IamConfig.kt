package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.iam")
data class IamConfig(val baseUrl: String = "", val path: String = "/accounts/register")