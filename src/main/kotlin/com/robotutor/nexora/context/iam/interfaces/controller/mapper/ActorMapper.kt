package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.context.iam.application.command.AuthenticateActorCommand
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateActorRequest
import com.robotutor.nexora.shared.domain.vo.PremisesId

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

    fun toAuthenticateActorCommand(
        authenticateActorRequest: AuthenticateActorRequest,
        accountData: AccountData,
        token: String
    ): AuthenticateActorCommand {
        return AuthenticateActorCommand(
            premisesId = PremisesId(authenticateActorRequest.premisesId),
            token = TokenValue(token.removePrefix("Bearer ")),
            accountData = accountData
        )
    }
}