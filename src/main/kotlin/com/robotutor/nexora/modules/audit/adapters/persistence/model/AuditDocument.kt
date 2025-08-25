package com.robotutor.nexora.modules.audit.adapters.persistence.model

import com.robotutor.nexora.shared.audit.model.AuditStatus
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
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
    val actorId: String? = null,
    val identifier: Identifier<ActorPrincipalType>? = null,
    @Indexed
    val premisesId: String? = null,
    val status: AuditStatus,
    val metadata: Map<String, Any?>,
    val timestamp: Instant,
    @Version
    val version: Long? = null
)
//{
//
//    fun toDomainModel(): Audit {
//        return Audit(
//            auditId = AuditId(this.auditId),
//            event = this.event,
//            actorId = this.actorId?.let { ActorId(actorId) },
//            identifier = this.identifier,
//            premisesId = this.premisesId?.let { PremisesId(premisesId) },
//            status = this.status,
//            metadata = this.metadata,
//            timestamp = this.timestamp,
//            version = this.version,
//        )
//    }
//
//    companion object {
//        fun from(audit: Audit): AuditDocument {
//            return AuditDocument(
//                auditId = audit.auditId.value,
//                actorId = audit.actorId.let { it?.value },
//                identifier = audit.identifier,
//                premisesId = audit.premisesId.let { it?.value },
//                status = audit.status,
//                metadata = audit.metadata,
//                event = audit.event,
//                timestamp = audit.timestamp,
//                version = audit.version
//            )
//        }
//    }
//}