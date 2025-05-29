package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.feed-service")
data class FeedConfig(
    val baseUrl: String,
    val feedsBatch: String = "/feeds/batch",
)