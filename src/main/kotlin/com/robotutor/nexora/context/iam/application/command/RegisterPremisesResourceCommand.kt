package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterPremisesResourceCommand(
    val premisesId: PremisesId,
    val name: Name,
    val owner: AccountData
) : Command
