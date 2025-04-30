package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val INVITATION_COLLECTION = "invitations"

@TypeAlias("Invitation")
@Document(INVITATION_COLLECTION)
data class Invitation(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val invitationType: InvitationType,
    val metaData: InvitationMetaData,
    val createdBy: UserId,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun deviceInvitation(
            invitationId: InvitationId,
            invitationRequest: DeviceInvitationRequest,
            userData: PremisesActorData
        ): Invitation {
            return Invitation(
                invitationId = invitationId,
                premisesId = userData.premisesId,
                createdBy = userData.actorId,
                invitationType = InvitationType.DEVICE,
                metaData = InvitationMetaData(name = invitationRequest.name, modelNo = invitationRequest.modelNo),
            )
        }

        fun userInvitation(
            invitationId: InvitationId,
            invitationRequest: UserInvitationRequest,
            userData: PremisesActorData
        ): Invitation {
            return Invitation(
                invitationId = invitationId,
                premisesId = userData.premisesId,
                invitationType = InvitationType.USER,
                metaData = InvitationMetaData(
                    authUserId = invitationRequest.userId,
                    roles = invitationRequest.roles,
                ),
                createdBy = userData.actorId,
            )

        }
    }
}

data class InvitationMetaData(
    val name: String? = null,
    val modelNo: String? = null,
    val authUserId: String? = null,
    val roles: List<String>? = null
)

enum class InvitationType {
    DEVICE,
    USER
}

typealias InvitationId = String