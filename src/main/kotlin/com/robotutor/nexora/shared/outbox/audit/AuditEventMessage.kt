package com.robotutor.nexora.shared.outbox.audit

import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

data class AuditEventMessage(
    val userId: String?,
    val deviceId: String?,
    val actorId: String?,
    val premisesId: String? = null,
    val action: String,
    val resource: ResourceMessage,
    val state: AuditState,
    val metadata: Map<String, Any?> = emptyMap(),
) : EventMessage {
    override val eventName: EventName = EventName.AUDITORY
}

data class ResourceMessage(
    val type: ResourceType,
    val identifier: String,
)
