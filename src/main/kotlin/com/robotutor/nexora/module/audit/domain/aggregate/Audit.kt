package com.robotutor.nexora.module.audit.domain.aggregate

import com.robotutor.nexora.module.audit.domain.vo.AuditId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.outbox.audit.AuditState
import com.robotutor.nexora.shared.outbox.audit.ResourceMessage
import com.robotutor.nexora.shared.outbox.vo.EventId
import java.time.Instant

data class Audit(
    val auditId: AuditId,
    val eventId: EventId,
    val userId: String?,
    val deviceId: String?,
    val actorId: String?,
    val premisesId: String?,
    val action: String,
    val resource: ResourceMessage,
    val state: AuditState,
    val metadata: Map<String, Any?> = emptyMap(),
    val createdAt: Instant = Instant.now(),
    val occurredAt: Instant,
) : AggregateRoot<Audit, AuditId, Event>(auditId)
