package com.robotutor.nexora.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.auth.iam-service")
data class IAMConfig(val baseUrl: String, val actorPath: String = "/iam/actors/{actorId}/roles/{roleId}")
