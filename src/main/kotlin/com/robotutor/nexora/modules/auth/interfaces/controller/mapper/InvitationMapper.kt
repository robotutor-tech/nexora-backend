package com.robotutor.nexora.modules.auth.interfaces.controller.mapper

import com.robotutor.nexora.modules.auth.application.command.InvitationCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponse
import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationResponse
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationWithTokenResponse
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

object InvitationMapper {
    fun toInvitationCommand(invitationRequest: InvitationRequest): InvitationCommand {
        return InvitationCommand(
            zoneId = ZoneId(invitationRequest.zoneId),
            name = Name(invitationRequest.name)
        )
    }

    fun toInvitationWithTokenResponse(pair: Pair<Invitation, TokenResponse>): InvitationWithTokenResponse {
        return InvitationWithTokenResponse(
            invitationId = pair.first.invitationId.value,
            premisesId = pair.first.premisesId.value,
            name = pair.first.name.value,
            token = "Bearer " + pair.second.value,
            invitedBy = pair.first.invitedBy.value,
            zoneId = pair.first.zoneId.value,
        )
    }

    fun toInvitationResponse(invitation: Invitation): InvitationResponse {
        return InvitationResponse(
            invitationId = invitation.invitationId.value,
            premisesId = invitation.premisesId.value,
            name = invitation.name.value,
            zoneId = invitation.zoneId.value,
            invitedBy = invitation.invitedBy.value,
        )
    }

}