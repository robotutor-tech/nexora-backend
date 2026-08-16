package com.robotutor.nexora.module.audit.infrastructure.persistence.mapper

import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import com.robotutor.nexora.module.audit.domain.vo.AuditId
import com.robotutor.nexora.module.audit.infrastructure.persistence.document.AuditDocument
import com.robotutor.nexora.shared.outbox.vo.EventId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object AuditDocumentMapper : DocumentMapper<Audit, AuditDocument> {

    override fun toMongoDocument(domain: Audit): AuditDocument {
        return AuditDocument(
            id = domain.getObjectId(),
            auditId = domain.auditId.value,
            eventId = domain.eventId.value,
            action = domain.action,
            resource = domain.resource,
            state = domain.state,
            principalData = domain.principalData,
            principalId = domain.principalId,
            principalType = domain.principalType,
            metadata = domain.metadata,
            createdAt = domain.createdAt,
            occurredAt = domain.occurredAt,
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: AuditDocument): Audit {
        return Audit(
            eventId = EventId(document.eventId),
            auditId = AuditId(document.auditId),
            action = document.action,
            resource = document.resource,
            state = document.state,
            metadata = document.metadata,
            createdAt = document.createdAt,
            occurredAt = document.occurredAt,
            principalId = document.principalId,
            principalType = document.principalType,
            principalData = document.principalData
        )
            .setObjectIdAndVersion(document.id, document.version)
    }
}
