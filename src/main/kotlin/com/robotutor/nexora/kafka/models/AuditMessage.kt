package com.robotutor.nexora.kafka.models

import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import java.time.LocalDateTime


data class AuditMessage(
    val status: AuditStatus,
    val actorId: ActorId?,
    val identifier: Identifier<ActorIdentifier>?,
    val metadata: Map<String, Any?>,
    val event: String,
    val accountId: String? = null,
    val premisesId: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class AuditStatus {
    SUCCESS,
    FAILURE,
}

typealias KafkaTopicName = String