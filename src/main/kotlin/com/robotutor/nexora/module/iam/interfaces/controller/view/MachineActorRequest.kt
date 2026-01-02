package com.robotutor.nexora.module.iam.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class MachineActorRequest(
    @field:NotBlank(message = "CredentialId should be valid")
    val premisesId: String,
    @field:NotBlank(message = "CredentialId should be valid")
    val deviceId: String
)