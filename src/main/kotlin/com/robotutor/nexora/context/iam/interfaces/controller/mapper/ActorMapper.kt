package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse

object ActorMapper {
    fun toActorResponse(actorAggregate: ActorAggregate): ActorResponse {
        return ActorResponse(
            actorId = actorAggregate.actorId.value,
            accountId = actorAggregate.accountId.value,
            premisesId = actorAggregate.premisesId.value,
            status = actorAggregate.status,
            createdAt = actorAggregate.createdAt,
            updatedAt = actorAggregate.updatedAt
        )
    }
}