package com.robotutor.nexora.modules.auth.adapters.persistence.model

import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.model.InvitationStatus
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val INVITATION_COLLECTION = "invitations"

@TypeAlias("Invitation")
@Document(INVITATION_COLLECTION)
data class InvitationDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val invitationId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val zoneId: String,
    val invitedBy: String,
    val createdAt: Instant,
    val status: InvitationStatus,
    @Version
    val version: Long?
) {

    fun toDomainModel(): Invitation {
        return Invitation(
            invitationId = InvitationId(invitationId),
            premisesId = PremisesId(premisesId),
            name = Name(name),
            zoneId = ZoneId(zoneId),
            invitedBy = ActorId(invitedBy),
            createdAt = createdAt,
            status = status,
            version = version
        )
    }

    companion object {
        fun from(invitation: Invitation): InvitationDocument {
            return InvitationDocument(
                invitationId = invitation.invitationId.value,
                premisesId = invitation.premisesId.value,
                name = invitation.name.value,
                zoneId = invitation.zoneId.value,
                invitedBy = invitation.invitedBy.value,
                createdAt = invitation.createdAt,
                status = invitation.status,
                version = invitation.version,
            )
        }
    }
}