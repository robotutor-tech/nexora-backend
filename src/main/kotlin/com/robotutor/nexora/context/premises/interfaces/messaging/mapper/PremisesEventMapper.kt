package com.robotutor.nexora.context.premises.interfaces.messaging.mapper

import com.robotutor.nexora.context.premises.application.command.ActivatePremisesCommand
import com.robotutor.nexora.context.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesActivateMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesCompensateMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId

object PremisesEventMapper {
    fun toActivatePremisesCommand(
        eventMessage: PremisesActivateMessage,
        accountData: AccountData
    ): ActivatePremisesCommand {
        return ActivatePremisesCommand(PremisesId(eventMessage.premisesId), accountData.accountId)
    }

    fun toCompensatePremisesRegistrationCommand(eventMessage: PremisesCompensateMessage, accountData: AccountData): CompensatePremisesRegistrationCommand {
        return CompensatePremisesRegistrationCommand(PremisesId(eventMessage.premisesId), accountData.accountId)
    }
}