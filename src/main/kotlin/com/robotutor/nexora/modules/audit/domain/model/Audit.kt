package com.robotutor.nexora.modules.audit.domain.model

import com.robotutor.nexora.shared.audit.model.AuditStatus
import com.robotutor.nexora.shared.domain.event.DomainModel
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import java.time.Instant

data class Audit(
    val auditId: AuditId,
    val event: String,
    val premisesId: PremisesId? = null,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant = Instant.now(),
    val version: Long? = null
) : DomainModel
