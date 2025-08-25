package com.robotutor.nexora.shared.audit.model

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType

data class AuditEvent(
    val event: String,
    val premisesId: PremisesId? = null,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
) : DomainEvent()

enum class AuditStatus {
    SUCCESS, FAILURE
}