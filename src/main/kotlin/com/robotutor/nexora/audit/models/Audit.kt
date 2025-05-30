package com.robotutor.nexora.audit.models

import com.robotutor.nexora.kafka.models.AuditMessage
import com.robotutor.nexora.kafka.models.AuditStatus
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val AUDIT_COLLECTION = "audit"

@TypeAlias("Audit")
@Document(AUDIT_COLLECTION)
data class Audit(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val auditId: AuditId,
    val event: String,
    val actorId: ActorId? = null,
    val identifier: Identifier<ActorIdentifier>? = null,
    val premisesId: PremisesId? = null,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: LocalDateTime,
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(auditId: String, message: AuditMessage): Audit {
            return Audit(
                auditId = auditId,
                actorId = message.actorId,
                identifier = message.identifier,
                premisesId = message.premisesId,
                status = message.status,
                metadata = message.metadata,
                event = message.event,
                timestamp = message.timestamp,
            )
        }
    }
}


typealias AuditId = String


// sign up ==> Auth user
// login ==> auth user
// Premise Actor ==>