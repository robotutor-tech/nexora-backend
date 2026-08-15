package com.robotutor.nexora.module.identity.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class AuthenticateAccountRequest(
    val credentialId: String,
    val secret: String
)

data class AuthenticateActorRequest(
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: String,
)
