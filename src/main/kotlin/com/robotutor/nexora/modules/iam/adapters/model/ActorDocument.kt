package com.robotutor.nexora.modules.iam.adapters.model

import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.model.ActorState
import com.robotutor.nexora.shared.domain.model.*
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
data class ActorDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actorId: String,
    @Indexed
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContext,
    val roleIds: List<String>,
    val state: ActorState,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) {

    fun toDomainModel(): Actor {
        return Actor(
            actorId = ActorId(actorId),
            premisesId = PremisesId(premisesId),
            roleIds = roleIds.map { RoleId(it) },
            state = state,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version,
            principalType = principalType,
            principal = principal,
        )
    }

    companion object {
        fun from(actor: Actor): ActorDocument {
            return ActorDocument(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                roleIds = actor.roleIds.map { it.value },
                state = actor.state,
                createdAt = actor.createdAt,
                updatedAt = actor.updatedAt,
                version = actor.version,
                principalType = actor.principalType,
                principal = actor.principal,
            )
        }
    }
}
