package com.robotutor.nexora.module.audit.interfaces.messaging.mapper

import com.robotutor.nexora.module.audit.application.service.command.AddAuditCommand
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.outbox.audit.AuditEventMessage
import com.robotutor.nexora.shared.outbox.audit.ResourceMessage
import com.robotutor.nexora.shared.outbox.persistence.mapper.PrincipalDataDocumentMapper

object AuditMapper {
    fun toAddAuditCommand(message: AuditEventMessage, context: MessageContext): AddAuditCommand {
        return AddAuditCommand(
            eventId = context.eventId,
            action = message.action,
            resource = ResourceMessage(message.resource.type, message.resource.identifier),
            state = message.state,
            metadata = message.metadata,
            occurredAt = context.occurredAt,
            principalId = message.principalId,
            principalType = message.principalType,
            principalData = context.principalData?.let { PrincipalDataDocumentMapper.toDocument(context.principalData) },
        )
    }
}
