package com.robotutor.nexora.orchestration.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.orchestration.iam")
data class IamConfig(
    val baseUrl: String = "",
    val registerHumanAccountPath: String = "/iam/accounts/register",
    val registerMachineAccountPath: String = "/iam/accounts/register/machine",
    val premisesOwnerRegisterPath: String = "/iam/premises-owners/register",
    val getActorPath: String = "/iam/actors"
)