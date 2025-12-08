package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.ActorStatus
import com.robotutor.nexora.context.iam.domain.vo.PermissionOverride
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ACTOR_COLLECTION = "actors"

@Document(ACTOR_COLLECTION)
@TypeAlias("Actor")
data class ActorDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actorId: String,
    @Indexed
    val premisesId: String,
    val accountId: String,
    val roleIds: Set<String>,
    val groupIds: Set<String>,
    val overrides: Set<PermissionOverride>,
    val status: ActorStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null,
) : MongoDocument<ActorAggregate>