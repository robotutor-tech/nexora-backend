package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.context.iam.application.command.AuthenticateActorCommand
import com.robotutor.nexora.context.iam.application.command.RegisterMachineActorCommand
import com.robotutor.nexora.context.iam.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateActorRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.MachineActorRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.OwnerCreationRequest
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId

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
        AccountData: AccountData,
        token: String
    ): AuthenticateActorCommand {
        return AuthenticateActorCommand(
            premisesId = PremisesId(authenticateActorRequest.premisesId),
            token = TokenValue(token.removePrefix("Bearer ")),
            accountData = AccountData
        )
    }

    fun toRegisterOwnerCommand(
        eventMessage: OwnerCreationRequest,
        AccountData: AccountData
    ): RegisterPremisesOwnerCommand {
        return RegisterPremisesOwnerCommand(
            premisesId = PremisesId(eventMessage.premisesId),
            owner = AccountData
        )
    }

    fun toRegisterMachineActorCommand(actorRequest: MachineActorRequest, AccountData: AccountData): RegisterMachineActorCommand {
        return RegisterMachineActorCommand(
            premisesId = PremisesId(actorRequest.premisesId),
            owner = AccountData,
            deviceId = ResourceId(actorRequest.deviceId)
        )
    }
}