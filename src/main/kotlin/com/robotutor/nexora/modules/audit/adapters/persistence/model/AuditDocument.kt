package com.robotutor.nexora.modules.audit.adapters.persistence.model

import com.robotutor.nexora.modules.audit.domain.model.Audit
import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
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
) : MongoDocument<Audit>
