package com.robotutor.nexora.modules.iam.infrastructure.persistence.document

import com.robotutor.nexora.modules.iam.domain.entity.Actor
import com.robotutor.nexora.modules.iam.domain.entity.ActorState
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.infrastructure.persistence.document.ActorPrincipalDocument
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("actors")
@TypeAlias("Actor")
data class ActorDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actorId: String,
    @Indexed
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalDocument,
    val roleIds: List<String>,
    val state: ActorState,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Actor>