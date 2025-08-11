package com.robotutor.nexora.modules.audit.adapters.inbound.messaging.mapper

import com.robotutor.nexora.modules.audit.adapters.inbound.messaging.dto.AuditMessage
import com.robotutor.nexora.modules.audit.domain.model.AuditDetails

object AuditMapper {
    fun toAuditDetails(auditMessage: AuditMessage): AuditDetails {
        return AuditDetails(
            event = auditMessage.event,
            actorId = auditMessage.actorId,
            identifier = auditMessage.identifier,
            premisesId = auditMessage.premisesId,
            status = auditMessage.status,
            metadata = auditMessage.metadata,
            timestamp = auditMessage.timestamp,
        )
    }

}