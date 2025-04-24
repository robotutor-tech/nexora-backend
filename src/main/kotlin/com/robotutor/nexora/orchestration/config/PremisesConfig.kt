package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.premises-service")
data class PremisesConfig(val baseUrl: String, val register: String = "/premises")