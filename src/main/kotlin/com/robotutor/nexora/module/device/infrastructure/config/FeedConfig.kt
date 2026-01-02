package com.robotutor.nexora.module.device.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.feed")
data class FeedConfig(
    val baseUrl: String,
    val registerPath: String = "/feeds",
)

