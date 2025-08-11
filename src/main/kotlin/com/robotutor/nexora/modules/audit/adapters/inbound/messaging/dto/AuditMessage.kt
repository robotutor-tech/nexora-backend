package com.robotutor.nexora.modules.audit.adapters.inbound.messaging.dto

import com.robotutor.nexora.modules.audit.domain.model.AuditStatus
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.Identifier
import java.time.Instant

data class AuditMessage(
    val status: AuditStatus,
    val actorId: ActorId?,
    val identifier: Identifier<ActorIdentifier>?,
    val metadata: Map<String, Any?>,
    val event: String,
    val accountId: String? = null,
    val premisesId: String? = null,
    val timestamp: Instant = Instant.now()
)
