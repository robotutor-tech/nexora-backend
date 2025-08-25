package com.robotutor.nexora.modules.audit.application.command

import com.robotutor.nexora.shared.audit.model.AuditStatus
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.shared.domain.model.PremisesId
import java.time.Instant

data class CreateAuditCommand(
    val event: String,
    val actorId: ActorId?,
    val identifier: Identifier<ActorPrincipalType>?,
    val premisesId: PremisesId?,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant
)
