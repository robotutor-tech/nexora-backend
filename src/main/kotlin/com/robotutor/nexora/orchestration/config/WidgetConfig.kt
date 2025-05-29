package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.widget-service")
data class WidgetConfig(
    val baseUrl: String,
    val widgetsBatch: String = "/widgets/batch",
)