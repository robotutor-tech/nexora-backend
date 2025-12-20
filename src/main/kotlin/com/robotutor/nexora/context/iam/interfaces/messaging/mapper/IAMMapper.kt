package com.robotutor.nexora.context.iam.interfaces.messaging.mapper

import com.robotutor.nexora.context.iam.application.command.ActivateAccountCommand
import com.robotutor.nexora.context.iam.application.command.CompensateAccountCommand
import com.robotutor.nexora.context.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountActivatedMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountCompensateMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.PremisesRegisteredMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

object IAMMapper {
    fun toAccountActivatedCommand(eventMessage: AccountActivatedMessage): ActivateAccountCommand {
        return ActivateAccountCommand(accountId = AccountId(eventMessage.accountId))
    }

    fun toCompensateAccountCommand(eventMessage: AccountCompensateMessage): CompensateAccountCommand {
        return CompensateAccountCommand(accountId = AccountId(eventMessage.accountId))
    }

    fun toRegisterPremisesResourceCommand(
        eventMessage: PremisesRegisteredMessage,
        accountData: AccountData
    ): RegisterPremisesResourceCommand {
        return RegisterPremisesResourceCommand(
            premisesId = PremisesId(eventMessage.premisesId),
            name = Name(eventMessage.name),
            owner = accountData
        )
    }
}