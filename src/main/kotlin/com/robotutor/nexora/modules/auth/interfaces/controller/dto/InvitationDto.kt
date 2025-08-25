package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import jakarta.validation.constraints.NotBlank

data class InvitationRequest(
    @field:NotBlank(message = "Zone is required")
    val zoneId: String,
    @field:NotBlank(message = "Device Name is required")
    val name: String,
)


data class InvitationWithTokenResponse(
    val invitationId: String,
    val premisesId: String,
    val name: String,
    val token: String,
    val invitedBy: String,
    val zoneId: String
)

data class InvitationResponse(
    val invitationId: String,
    val premisesId: String,
    val name: String,
    val invitedBy: String,
    val zoneId: String
)

