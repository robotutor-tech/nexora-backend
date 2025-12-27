package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterPremisesOwnerCommand(val premisesId: PremisesId, val owner: AccountData) : Command
