package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.UserId
import com.robotutor.nexora.modules.auth.models.DeviceInvitation
import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.auth.models.Token
import com.robotutor.nexora.modules.auth.models.UserInvitation
import com.robotutor.nexora.modules.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeviceInvitationRequest(
    @field:NotBlank(message = "Zone is required")
    val zoneId: String,
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

data class DeviceInvitationView(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: String,
    val token: String,
    val invitedBy: ActorId,
    val zoneId: String
) {
    companion object {
        fun from(deviceInvitation: DeviceInvitation, token: Token?): DeviceInvitationView {
            return DeviceInvitationView(
                invitationId = deviceInvitation.invitationId,
                premisesId = deviceInvitation.premisesId,
                invitedBy = deviceInvitation.invitedBy,
                token = token?.value?.let { "Bearer $it" } ?: "",
                name = deviceInvitation.name,
                zoneId = deviceInvitation.zoneId
            )
        }
    }
}

data class UserInvitationView(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val userId: UserId,
    val createdBy: UserId
) {
    companion object {
        fun from(userInvitation: UserInvitation): UserInvitationView {
            return UserInvitationView(
                invitationId = userInvitation.invitationId,
                premisesId = userInvitation.premisesId,
                createdBy = userInvitation.invitedBy,
                userId = userInvitation.userId
            )
        }
    }
}