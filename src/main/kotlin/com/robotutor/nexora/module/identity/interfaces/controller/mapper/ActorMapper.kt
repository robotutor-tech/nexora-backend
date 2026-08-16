package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.application.command.AuthenticateActorCommand
import com.robotutor.nexora.module.identity.application.command.RegisterMachineActorCommand
import com.robotutor.nexora.module.identity.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.module.identity.domain.vo.TokenValue
import com.robotutor.nexora.module.identity.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.module.identity.interfaces.controller.view.AuthenticateActorRequest
import com.robotutor.nexora.module.identity.interfaces.controller.view.MachineActorRequest
import com.robotutor.nexora.module.identity.interfaces.controller.view.OwnerCreationRequest
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.UserData

object ActorMapper {
    fun toActorResponse(actor: Actor): ActorResponse {
        return ActorResponse(
            actorId = actor.actorId.value,
            accountId = actor.accountId.value,
            premisesId = actor.premisesId.value,
            status = actor.status,
            createdAt = actor.createdAt,
            updatedAt = actor.updatedAt
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

    fun toRegisterOwnerCommand(
        eventMessage: OwnerCreationRequest,
        userData: UserData,
    ): RegisterPremisesOwnerCommand {
        return RegisterPremisesOwnerCommand(
            premisesId = PremisesId(eventMessage.premisesId),
            owner = userData
        )
    }

    fun toRegisterMachineActorCommand(
        actorRequest: MachineActorRequest,
        deviceData: DeviceData,
    ): RegisterMachineActorCommand {
        return RegisterMachineActorCommand(
            premisesId = PremisesId(actorRequest.premisesId),
            owner = deviceData,
            deviceId = ResourceId(actorRequest.deviceId)
        )
    }
}
