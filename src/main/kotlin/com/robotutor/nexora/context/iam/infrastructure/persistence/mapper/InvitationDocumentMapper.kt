package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.entity.Invitation
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.InvitationDocument
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object InvitationDocumentMapper : DocumentMapper<Invitation, InvitationDocument> {
    override fun toMongoDocument(domain: Invitation): InvitationDocument {
        return InvitationDocument(
            id = domain.getObjectId(),
            invitationId = domain.invitationId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            zoneId = domain.zoneId.value,
            invitedBy = domain.invitedBy.value,
            createdAt = domain.createdAt,
            status = domain.status,
            tokenId = domain.sessionId.value,
            version = domain.getVersion(),
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
            sessionId = SessionId(document.tokenId)
        ).setObjectIdAndVersion(document.id, document.version)
    }
}

