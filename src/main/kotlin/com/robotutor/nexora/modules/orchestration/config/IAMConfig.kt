package com.robotutor.nexora.modules.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.iam-service")
data class IAMConfig(
    val baseUrl: String,
    val registerPremises: String = "/iam/premises/register",
    val registerDevice: String = "/iam/premises/register/device",
    val policyBatchPath: String = "/iam/policies/batch",
    val actorsPath: String = "/iam/actors"
)