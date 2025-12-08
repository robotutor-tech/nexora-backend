package com.robotutor.nexora.context.iam.interfaces.messaging.mapper

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.context.iam.interfaces.messaging.message.PremisesRegisteredMessage
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

object IAMMapper {
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