package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.DeviceRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.security.models.UserPremisesData
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
    val tokenId: TokenId,
    val name: String,
    val modelNo: String,
    val createdBy: UserId,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(
            invitationId: InvitationId,
            tokenId: TokenId,
            deviceRequest: DeviceRequest,
            userData: UserPremisesData
        ): Invitation {
            return Invitation(
                invitationId = invitationId,
                premisesId = userData.premisesId,
                name = deviceRequest.name,
                modelNo = deviceRequest.modelNo,
                tokenId = tokenId,
                createdBy = userData.userId,
            )
        }
    }
}

typealias InvitationId = String