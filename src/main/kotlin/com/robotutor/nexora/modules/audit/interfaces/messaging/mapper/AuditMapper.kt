package com.robotutor.nexora.modules.audit.interfaces.messaging.mapper

import com.robotutor.nexora.modules.audit.application.command.CreateAuditCommand
import com.robotutor.nexora.modules.audit.interfaces.messaging.dto.AuditMessage

object AuditMapper {
    fun toCreateAuditCommand(auditMessage: AuditMessage): CreateAuditCommand {
        return CreateAuditCommand(
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