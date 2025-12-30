package com.robotutor.nexora.common.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.security")
data class AppConfig(
    val internalAccessToken: String,
    val unSecuredPath: List<String> = emptyList(),
    val iamBaseUrl: String = "",
    val validatePath: String = "/iam/sessions/validate",
    val accountPath: String = "/iam/accounts/{accountId}",
    val authorizeResourcePath: String = "/iam/resources/authorize",
    val resourcePath: String = "/iam/resources"
)