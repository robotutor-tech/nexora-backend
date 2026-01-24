package com.robotutor.nexora.module.automation.infrastructure.facade.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.automation")
data class AutomationConfig(
    val feedBaseUrl: String,
    val feedPath: String = "/feeds/{feedId}",
)