package com.robotutor.nexora.modules.audit.adapters.persistence.mapper

import com.robotutor.nexora.modules.audit.adapters.persistence.model.AuditDocument
import com.robotutor.nexora.modules.audit.domain.model.Audit
import com.robotutor.nexora.modules.audit.domain.model.AuditId
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Component

@Component
class AuditDocumentMapper : DocumentMapper<Audit, AuditDocument> {
    override fun toDomainModel(document: AuditDocument): Audit {
        val principalType = TokenPrincipalType.valueOf(document.actorId ?: "UNKNOWN")
        val principal = UserContext(UserId(""))
        return Audit(
            auditId = AuditId(document.auditId),
            event = document.event,
            premisesId = document.premisesId?.let { PremisesId(it) },
            principalType = principalType,
            principal = principal,
            status = document.status,
            metadata = document.metadata,
            timestamp = document.timestamp,
            version = document.version
        )
    }

    override fun toMongoDocument(domain: Audit): AuditDocument {
        val actorId = domain.principalType.name
        val identifier: Identifier<ActorPrincipalType>? = null // TODO: Map from principal if needed
        return AuditDocument(
            auditId = domain.auditId.value,
            event = domain.event,
            actorId = actorId,
            identifier = identifier,
            premisesId = domain.premisesId?.value,
            status = domain.status,
            metadata = domain.metadata,
            timestamp = domain.timestamp,
            version = domain.version
        )
    }
}

