package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.feed-service")
data class FeedConfig(
    val baseUrl: String,
    val feedsBatch: String = "/feeds/batch",
)