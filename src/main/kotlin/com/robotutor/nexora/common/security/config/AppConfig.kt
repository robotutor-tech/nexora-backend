package com.robotutor.nexora.common.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.security")
data class AppConfig(
    val internalAccessToken: String,
    val opaBaseUrl: String,
    val unSecuredPath: List<String> = emptyList(),
    val validatePath: String = "/auth/tokens/validate",
    val invitationDevicesPath: String = "/auth/invitations/{invitationId}/devices",
    val actorPath: String = "/iam/actors/{actorId}/roles/{roleId}",
    val entitlementPath: String = "/iam/entitlements",
    val opaPath: String = "/v1/data/authz/allow"
)