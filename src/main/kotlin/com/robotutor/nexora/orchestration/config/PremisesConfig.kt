package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.premises")
data class PremisesConfig(val baseUrl: String = "", val premisesPath: String = "/premises")