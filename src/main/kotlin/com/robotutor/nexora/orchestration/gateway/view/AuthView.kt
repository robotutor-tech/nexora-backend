package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId

data class InvitationView(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val token: String,
    val name: String,
    val modelNo: String,
    val createdBy: UserId,
)
