package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
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
import java.time.Instant
import java.time.ZoneOffset

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
    val roles: MutableSet<RoleId>,
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
                roles = request.roles.toMutableSet(),
                state = ActorState.ACTIVE,
            )
        }
    }
}

enum class ActorState {
    ACTIVE,
    INACTIVE,
}

