package com.robotutor.nexora.iam.models

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

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
    val actorIdentifier: Identifier<ActorIdentifier>,
    val roleId: RoleId,
    val state: ActorState,
    val policies: List<PolicyId> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(actorId: ActorId, premisesId: PremisesId, id: String, type: ActorIdentifier, roleId: RoleId): Actor {
            return Actor(
                actorId = actorId,
                premisesId = premisesId,
                actorIdentifier = Identifier(id, type),
                roleId = roleId,
                state = ActorState.ACTIVE,
            )
        }
    }
}

enum class ActorState {
    ACTIVE,
    INACTIVE,
}

