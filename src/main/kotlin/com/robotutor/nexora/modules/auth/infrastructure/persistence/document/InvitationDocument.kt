package com.robotutor.nexora.modules.auth.infrastructure.persistence.document

import com.robotutor.nexora.modules.auth.domain.entity.Invitation
import com.robotutor.nexora.modules.auth.domain.entity.InvitationStatus
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
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
    val tokenId: String,
    val name: String,
    val zoneId: String,
    val invitedBy: String,
    val createdAt: Instant,
    val status: InvitationStatus,
    @Version
    val version: Long?
) : MongoDocument<Invitation>
