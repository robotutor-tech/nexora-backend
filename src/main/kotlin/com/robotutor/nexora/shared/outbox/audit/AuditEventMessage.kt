package com.robotutor.nexora.shared.outbox.audit

import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.message.config.KafkaTopic
import com.robotutor.nexora.shared.message.message.EventMessage
import java.time.Instant
//
//data class AuditEventMessage(
////    override val userId: String,
//    val action: String,
//    val resource: ResourceMessage,
//    val state: AuditState,
//    val merchantId: String? = null,
//    val metadata: Map<String, Any?> = emptyMap(),
////    override val eventId: String,
////    override val correlationId: String,
////    override val occurredAt: Instant,
//) : EventMessage {
//    override val topic: KafkaTopic = KafkaTopic.AUDITORY
//}

data class ResourceMessage(
    val type: ResourceType,
    val identifier: String,
)
