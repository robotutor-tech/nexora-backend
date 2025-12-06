package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.entity.Invitation
import com.robotutor.nexora.context.iam.domain.entity.TokenId
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.InvitationDocument
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object InvitationDocumentMapper : DocumentMapper<Invitation, InvitationDocument> {
    override fun toMongoDocument(domain: Invitation): InvitationDocument {
        return InvitationDocument(
            id = null,
            invitationId = domain.invitationId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            zoneId = domain.zoneId.value,
            invitedBy = domain.invitedBy.value,
            createdAt = domain.createdAt,
            status = domain.status,
            tokenId = domain.tokenId.value,
            version = domain.version
        )
    }

    override fun toDomainModel(document: InvitationDocument): Invitation {
        return Invitation(
            invitationId = InvitationId(document.invitationId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            zoneId = ZoneId(document.zoneId),
            invitedBy = ActorId(document.invitedBy),
            createdAt = document.createdAt,
            status = document.status,
            version = document.version,
            tokenId = TokenId(document.tokenId)
        )
    }
}

