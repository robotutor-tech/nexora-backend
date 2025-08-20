package com.robotutor.nexora.modules.iam.models

import com.robotutor.nexora.modules.iam.controllers.view.RegisterActorRequest
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

const val ACTOR_COLLECTION = "actors"

@TypeAlias("Actor")
@Document(ACTOR_COLLECTION)
data class Actor(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actorId: ActorId,
    @Indexed
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val roles: List<RoleId>,
    val state: ActorState,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(actorId: ActorId, request: RegisterActorRequest): Actor {
            return Actor(
                actorId = actorId,
                premisesId = request.premisesId,
                identifier = request.identifier,
                roles = request.roles.toSet().toList(),
                state = ActorState.ACTIVE,
            )
        }
    }
}

enum class ActorState {
    ACTIVE,
    INACTIVE,
}

