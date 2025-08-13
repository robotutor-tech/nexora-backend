package com.robotutor.nexora.modules.auth.models

import com.robotutor.nexora.modules.auth.interfaces.controller.dto.DeviceInvitationRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.UserInvitationRequest
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

interface Invitation {
    fun markAsAccepted(): Invitation

    val invitationId: InvitationId
    val premisesId: PremisesId
    val invitedBy: ActorId
    val createdAt: Instant
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
    @Indexed
    override val premisesId: PremisesId,
    val name: String,
    val zoneId: String,
    override val invitedBy: ActorId,
    override val createdAt: Instant = Instant.now(),
    override var status: InvitationStatus = InvitationStatus.INVITED,
    @Version
    val version: Long? = null
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
    override val createdAt: Instant = Instant.now(),
    override var status: InvitationStatus = InvitationStatus.INVITED,
    @Version
    val version: Long? = null
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