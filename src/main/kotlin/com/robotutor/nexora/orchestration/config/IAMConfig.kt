package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.iam-service")
data class IAMConfig(val baseUrl: String, val registerPremises: String = "/iam/premises/register")