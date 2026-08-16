package com.robotutor.nexora.shared.outbox.audit

import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.outbox.persistence.document.PrincipalDataDocument

data class AuditEventMessage(
    val principalId: String,
    val principalType: String,
    val action: String,
    val resource: ResourceMessage,
    val state: AuditState,
    val premisesId: String? = null,
    val metadata: Map<String, Any?> = emptyMap(),
    val principalData: PrincipalDataDocument? = null,
) : EventMessage {
    override val eventName: EventName = EventName.AUDITORY
}

data class ResourceMessage(
    val type: ResourceType,
    val identifier: String,
)
