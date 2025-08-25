package com.robotutor.nexora.shared.adapters.messaging.models

import com.robotutor.nexora.shared.audit.model.AuditStatus
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.Identifier
import java.time.Instant

data class AuditMessage(
    val status: AuditStatus,
    val actorId: String?,
    val identifier: Identifier<ActorPrincipalType>?,
    val metadata: Map<String, Any?>,
    val event: String,
    val accountId: String? = null,
    val premisesId: String? = null,
    val timestamp: Instant = Instant.now()
)

