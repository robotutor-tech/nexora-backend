package com.robotutor.nexora.modules.audit.domain.model

import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.Identifier
import java.time.Instant

data class Audit(
    val auditId: AuditId,
    val event: String,
    val actorId: String? = null,
    val identifier: Identifier<ActorIdentifier>? = null,
    val premisesId: String? = null,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant = Instant.now(),
    val version: Long? = null
)

enum class AuditStatus {
    SUCCESS,
    FAILURE,
}
