package com.robotutor.nexora.module.audit.infrastructure.persistence.document

import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import com.robotutor.nexora.shared.outbox.audit.AuditState
import com.robotutor.nexora.shared.outbox.audit.ResourceMessage
import com.robotutor.nexora.shared.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("Audit")
@Document("audits")
data class AuditDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val auditId: String,
    @Indexed(unique = true)
    val eventId: String,
    val userId: String? = null,
    val deviceId: String? = null,
    val actorId: String? = null,
    val premisesId: String? = null,
    val action: String,
    val resource: ResourceMessage,
    val state: AuditState,
    val metadata: Map<String, Any?> = emptyMap(),
    val createdAt: Instant,
    val occurredAt: Instant,
    @Version
    val version: Long? = null,
) : MongoDocument<Audit>
