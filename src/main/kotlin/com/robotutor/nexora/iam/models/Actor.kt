package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.premises.models.PremisesId
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
    val type: ActorType,
    val identifier: String,
    val roleId: RoleId,
    val state: ActorState,
    val policies: List<PolicyId> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(actorId: String, request: RegisterActorRequest, roleId: RoleId): Actor {
            return Actor(
                actorId = actorId,
                premisesId = request.premisesId,
                type = request.type,
                identifier = request.identifier,
                roleId = roleId,
                state = ActorState.ACTIVE,
            )
        }
    }
}

enum class ActorType {
    HUMAN,
    DEVICE,
    LOCAL_SERVER,
    SERVER
}

enum class ActorState {
    ACTIVE,
    INACTIVE,
}

typealias ActorId = String