package com.robotutor.nexora.modules.iam.adapters.persistence.model

import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.model.ActorState
import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse
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
    val principal: PrincipalContextResponse,
    val roleIds: List<String>,
    val state: ActorState,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Actor>
