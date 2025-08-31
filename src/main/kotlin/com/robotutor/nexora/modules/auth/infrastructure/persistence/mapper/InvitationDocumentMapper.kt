package com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.infrastructure.persistence.model.InvitationDocument
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Component

@Component
class InvitationDocumentMapper : DocumentMapper<Invitation, InvitationDocument> {
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

