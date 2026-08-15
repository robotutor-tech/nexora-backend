package com.robotutor.nexora.module.premises.interfaces.messaging.mapper

import com.robotutor.nexora.module.premises.application.command.ActivatePremisesCommand
import com.robotutor.nexora.module.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.UserData

object PremisesEventMapper {
    fun toActivatePremisesCommand(
        eventMessage: PremisesOwnerRegisteredMessage,
        actorData: ActorData,
    ): ActivatePremisesCommand {
        return ActivatePremisesCommand(PremisesId(eventMessage.premisesId), actorData.accountData.accountId)
    }

    fun toCompensatePremisesRegistrationCommand(
        eventMessage: PremisesOwnerRegistrationFailedMessage,
        principalData: ActorData
    ): CompensatePremisesRegistrationCommand {
        return CompensatePremisesRegistrationCommand(
            PremisesId(eventMessage.premisesId),
            principalData.accountData.accountId
        )
    }
}
