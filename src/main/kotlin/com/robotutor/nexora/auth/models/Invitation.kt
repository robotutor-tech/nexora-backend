package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.zone.models.ZoneId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

interface Invitation {
    fun markAsAccepted(): Invitation

    val invitationId: InvitationId
    val premisesId: PremisesId
    val invitedBy: ActorId
    val createdAt: LocalDateTime
    var status: InvitationStatus
}

const val DEVICE_INVITATION_COLLECTION = "devicesInvitations"

@TypeAlias("DeviceInvitation")
@Document(DEVICE_INVITATION_COLLECTION)
data class DeviceInvitation(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    override val invitationId: InvitationId,
    override val premisesId: PremisesId,
    val name: String,
    val zoneId: ZoneId,
    override val invitedBy: ActorId,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override var status: InvitationStatus = InvitationStatus.INVITED,
) : Invitation {
    override fun markAsAccepted(): Invitation {
        this.status = InvitationStatus.ACCEPTED
        return this
    }

    companion object {
        fun from(
            invitationId: InvitationId,
            invitationRequest: DeviceInvitationRequest,
            userData: PremisesActorData
        ): DeviceInvitation {
            return DeviceInvitation(
                invitationId = invitationId,
                premisesId = userData.premisesId,
                invitedBy = userData.actorId,
                name = invitationRequest.name,
                zoneId = invitationRequest.zoneId,
            )
        }
    }
}

const val USER_INVITATION_COLLECTION = "usersInvitations"

@TypeAlias("UserInvitation")
@Document(USER_INVITATION_COLLECTION)
data class UserInvitation(
    @Id
    var id: ObjectId? = null,
    override val invitationId: InvitationId,
    override val premisesId: PremisesId,
    val userId: UserId,
    val roles: List<RoleId> = emptyList(),
    override val invitedBy: ActorId,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override var status: InvitationStatus = InvitationStatus.INVITED,
) : Invitation {
    override fun markAsAccepted(): Invitation {
        this.status = InvitationStatus.ACCEPTED
        return this
    }

    companion object {
        fun from(
            invitationId: InvitationId,
            invitationRequest: UserInvitationRequest,
            userData: PremisesActorData
        ): UserInvitation {
            return UserInvitation(
                invitationId = invitationId,
                premisesId = userData.premisesId,
                invitedBy = userData.actorId,
                userId = invitationRequest.userId,
                roles = invitationRequest.roles,
            )
        }
    }
}

enum class InvitationStatus {
    INVITED,
    ACCEPTED,
}

typealias InvitationId = String