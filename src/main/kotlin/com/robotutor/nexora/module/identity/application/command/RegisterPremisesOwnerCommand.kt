package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.UserData

data class RegisterPremisesOwnerCommand(val premisesId: PremisesId, val owner: UserData) : Command
