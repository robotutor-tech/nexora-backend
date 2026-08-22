package com.robotutor.nexora.module.identity.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class AuthenticateUserAccountRequest(
    val email: String,
    val password: String
)

data class AuthenticateActorRequest(
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: String,
)
