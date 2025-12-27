package com.robotutor.nexora.context.premises.interfaces.messaging.mapper

import com.robotutor.nexora.context.premises.application.command.ActivatePremisesCommand
import com.robotutor.nexora.context.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId

object PremisesEventMapper {
    fun toActivatePremisesCommand(
        eventMessage: PremisesOwnerRegisteredMessage,
        AccountData: AccountData
    ): ActivatePremisesCommand {
        return ActivatePremisesCommand(PremisesId(eventMessage.premisesId), AccountData.accountId)
    }

    fun toCompensatePremisesRegistrationCommand(
        eventMessage: PremisesOwnerRegistrationFailedMessage,
        AccountData: AccountData
    ): CompensatePremisesRegistrationCommand {
        return CompensatePremisesRegistrationCommand(PremisesId(eventMessage.premisesId), AccountData.accountId)
    }
}