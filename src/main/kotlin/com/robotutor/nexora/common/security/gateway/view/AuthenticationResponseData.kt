package com.robotutor.nexora.common.security.gateway.view

import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.Identifier
import com.robotutor.nexora.common.security.models.TokenIdentifier
import com.robotutor.nexora.modules.zone.models.ZoneId

data class AuthenticationResponseData(
    val identifier: Identifier<TokenIdentifier>,
    val roleId: RoleId?,
)

data class InvitationResponseData(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val invitedBy: ActorId,
    val name: String,
    val zoneId: ZoneId,
)