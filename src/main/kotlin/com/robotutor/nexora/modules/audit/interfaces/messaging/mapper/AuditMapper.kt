package com.robotutor.nexora.modules.audit.interfaces.messaging.mapper

import com.robotutor.nexora.modules.audit.application.command.CreateAuditCommand
import com.robotutor.nexora.modules.audit.interfaces.messaging.dto.AuditMessage
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId

object AuditMapper {
    fun toCreateAuditCommand(auditMessage: AuditMessage): CreateAuditCommand {
        return CreateAuditCommand(
            event = auditMessage.event,
            actorId = auditMessage.actorId?.let { ActorId(auditMessage.actorId) },
            identifier = auditMessage.identifier,
            premisesId = auditMessage.premisesId?.let { PremisesId(auditMessage.premisesId) },
            status = auditMessage.status,
            metadata = auditMessage.metadata,
            timestamp = auditMessage.timestamp,
        )
    }

}