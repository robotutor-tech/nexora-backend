package com.robotutor.nexora.modules.orchestration.gateway.view

import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.UserId

data class InvitationView(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val token: String,
    val name: String,
    val modelNo: String,
    val createdBy: UserId,
)
