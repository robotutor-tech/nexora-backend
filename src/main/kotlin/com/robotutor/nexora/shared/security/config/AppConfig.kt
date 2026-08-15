package com.robotutor.nexora.shared.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.security")
data class AppConfig(
    val refreshPath: String = "/refresh",
    val internalAccessToken: String,
    val unSecuredPath: List<String> = emptyList(),
    val IdentityBaseUrl: String = "",
    val validatePath: String = "/Identity/sessions/validate",
    val accountPath: String = "/Identity/accounts/{accountId}",
    val authorizeResourcePath: String = "/Identity/resources/authorize",
    val resourcePath: String = "/Identity/resources"
)
