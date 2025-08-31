package com.robotutor.nexora.modules.iam.infrastructure.persistence.document

import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.EntitlementStatus
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ENTITLEMENT_COLLECTION = "entitlements"

@TypeAlias("Entitlement")
@Document(ENTITLEMENT_COLLECTION)
data class EntitlementDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val entitlementId: String,
    @Indexed
    val roleId: String,
    @Indexed
    val premisesId: String,
    @Indexed
    val action: ActionType,
    @Indexed
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Entitlement>