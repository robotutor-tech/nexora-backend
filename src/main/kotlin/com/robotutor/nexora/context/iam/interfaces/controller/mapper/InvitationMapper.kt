package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.InvitationCommand
import com.robotutor.nexora.context.iam.domain.entity.Invitation
import com.robotutor.nexora.context.iam.interfaces.controller.view.InvitationRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.InvitationResponse
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId

object InvitationMapper {
    fun toInvitationCommand(invitationRequest: InvitationRequest): InvitationCommand {
        return InvitationCommand(
            zoneId = ZoneId(invitationRequest.zoneId), name = Name(invitationRequest.name)
        )
    }

//    fun toInvitationWithTokenResponse(pair: Pair<Invitation, TokenResponse>): InvitationWithTokenResponse {
//        return InvitationWithTokenResponse(
//            invitationId = pair.first.invitationId.value,
//            premisesId = pair.first.premisesId.value,
//            name = pair.first.name.value,
//            token = "Bearer " + pair.second.value,
//            invitedBy = pair.first.invitedBy.value,
//            zoneId = pair.first.zoneId.value,
//        )
//    }

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