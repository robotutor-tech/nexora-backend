package com.robotutor.nexora.modules.audit.application.command

import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.modules.audit.domain.model.AuditStatus
import java.time.Instant

data class CreateAuditCommand(
    val event: String,
    val actorId: ActorId?,
    val identifier: Identifier<ActorIdentifier>?,
    val premisesId: String?,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant
)
