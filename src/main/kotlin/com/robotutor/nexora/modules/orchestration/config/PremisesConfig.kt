package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.premises-service")
data class PremisesConfig(
    val baseUrl: String,
    val premises: String = "/premises",
    val premisesId: String = "/premises/{premisesId}"
)