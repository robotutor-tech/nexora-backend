package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration")
data class InternalAccessTokenConfig(val internalAccessToken: String)
