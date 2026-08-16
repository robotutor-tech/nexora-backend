package com.robotutor.nexora.module.audit.application.service.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.outbox.audit.AuditState
import com.robotutor.nexora.shared.outbox.audit.ResourceMessage
import com.robotutor.nexora.shared.outbox.persistence.document.PrincipalDataDocument
import com.robotutor.nexora.shared.outbox.vo.EventId
import java.time.Instant

data class AddAuditCommand(
    val principalId: String,
    val principalType: String,
    val eventId: EventId,
    val action: String,
    val resource: ResourceMessage,
    val metadata: Map<String, Any?> = emptyMap(),
    val principalData: PrincipalDataDocument? = null,
    val state: AuditState,
    val occurredAt: Instant,
) : Command



