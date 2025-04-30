package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.auth.models.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeviceInvitationRequest(
    @field:NotBlank(message = "Model No is required")
    val modelNo: String,
    @field:NotBlank(message = "Device Name is required")
    val name: String,
)

data class UserInvitationRequest(
    @field:NotBlank(message = "User id is required")
    val userId: UserId,
    @field:NotBlank(message = "Device Name is required")
    @field:Size(min = 1, max = 5, message = "Roles should be at least 1 or at max 5")
    val roles: List<String>,
)

data class InvitationView(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val invitationType: InvitationType,
    val metaData: InvitationMetaDataView,
    val token: String?,
    val createdBy: UserId,
) {
    companion object {
        fun from(token: Token, invitation: Invitation): InvitationView {
            return InvitationView(
                invitationId = invitation.invitationId,
                createdBy = invitation.createdBy,
                premisesId = invitation.premisesId,
                metaData = InvitationMetaDataView.from(invitation.metaData),
                invitationType = invitation.invitationType,
                token = if (invitation.invitationType == InvitationType.DEVICE) token.value else null
            )
        }
    }
}

data class InvitationMetaDataView(val name: String?, val modelNo: String?, val authUserId: String?) {
    companion object {
        fun from(metaData: InvitationMetaData): InvitationMetaDataView {
            return InvitationMetaDataView(
                name = metaData.name,
                modelNo = metaData.modelNo,
                authUserId = metaData.authUserId
            )
        }
    }
}