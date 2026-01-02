package com.robotutor.nexora.module.iam.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class AuthenticateAccountRequest(
    @field:NotBlank(message = "CredentialId should not be blank")
    val credentialId: String,
    @field:NotBlank(message = "Credential secret should not be blank")
    val secret: String
)

data class AuthenticateActorRequest(
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: String,
)
