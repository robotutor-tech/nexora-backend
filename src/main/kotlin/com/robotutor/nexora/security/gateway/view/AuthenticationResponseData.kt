package com.robotutor.nexora.security.gateway.view

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.TokenIdentifier
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.zone.models.ZoneId

data class AuthenticationResponseData(
    val tokenIdentifier: Identifier<TokenIdentifier>
)

data class InvitationResponseData(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val invitedBy: ActorId,
    val name: String,
    val zoneId: ZoneId,
)