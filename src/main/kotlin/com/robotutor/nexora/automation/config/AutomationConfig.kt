package com.robotutor.nexora.automation.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.automation")
data class AutomationConfig(
    val feedServiceBaseUrl: String,
    val feedByIdPath: String = "/feeds/{feedId}"
)