package com.robotutor.nexora.modules.audit.adapters.persistence.model

import com.robotutor.nexora.modules.audit.domain.model.Audit
import com.robotutor.nexora.modules.audit.domain.model.AuditId
import com.robotutor.nexora.modules.audit.domain.model.AuditStatus
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val AUDIT_COLLECTION = "audit"

@TypeAlias("Audit")
@Document(AUDIT_COLLECTION)
data class AuditDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val auditId: String,
    val event: String,
    val actorId: ActorId? = null,
    val identifier: Identifier<ActorIdentifier>? = null,
    @Indexed
    val premisesId: PremisesId? = null,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant,
    @Version
    val version: Long? = null
) {

    fun toDomainModel(): Audit {
        return Audit(
            auditId = AuditId(this.auditId),
            event = this.event,
            actorId = this.actorId,
            identifier = this.identifier,
            premisesId = this.premisesId,
            status = this.status,
            metadata = this.metadata,
            timestamp = this.timestamp,
            version = this.version,
        )
    }

    companion object {
        fun from(audit: Audit): AuditDocument {
            return AuditDocument(
                auditId = audit.auditId.value,
                actorId = audit.actorId,
                identifier = audit.identifier,
                premisesId = audit.premisesId,
                status = audit.status,
                metadata = audit.metadata,
                event = audit.event,
                timestamp = audit.timestamp,
                version = audit.version
            )
        }
    }
}