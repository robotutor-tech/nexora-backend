package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.SessionDocument
import com.robotutor.nexora.shared.outbox.persistence.mapper.PrincipalDataDocumentMapper
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object SessionDocumentMapper : DocumentMapper<Session, SessionDocument> {
    override fun toMongoDocument(domain: Session): SessionDocument {
        return SessionDocument(
            id = domain.getObjectId(),
            sessionId = domain.sessionId.value,
            accountDataDocument = PrincipalDataDocumentMapper.toDocument(domain.principalData),
            token = domain.token.value,
            status = domain.getStatus(),
            issuedAt = domain.issuedAt,
            expiresAt = domain.expiresAt,
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: SessionDocument): Session {
        return Session.create(
            sessionId = SessionId(document.sessionId),
            token = HashAccessToken(document.token),
            issuedAt = document.issuedAt,
            principalData = PrincipalDataDocumentMapper.toDomain(document.accountDataDocument),
            expiredAt = document.expiresAt,
            status = document.status,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
