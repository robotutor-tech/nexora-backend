package com.robotutor.nexora.context.premises.interfaces.messaging.mapper

import com.robotutor.nexora.context.premises.application.command.ActivatePremisesCommand
import com.robotutor.nexora.context.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId

object PremisesEventMapper {
    fun toActivatePremisesCommand(
        eventMessage: PremisesOwnerRegisteredMessage,
        accountData: AccountData
    ): ActivatePremisesCommand {
        return ActivatePremisesCommand(PremisesId(eventMessage.premisesId), accountData.accountId)
    }

    fun toCompensatePremisesRegistrationCommand(
        eventMessage: PremisesOwnerRegistrationFailedMessage,
        accountData: AccountData
    ): CompensatePremisesRegistrationCommand {
        return CompensatePremisesRegistrationCommand(PremisesId(eventMessage.premisesId), accountData.accountId)
    }
}