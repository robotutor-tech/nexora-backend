package com.robotutor.nexora.modules.premises.application.command

import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.UserId

data class CreatePremisesCommand(val name: String, val owner: UserData)
data class RegisterPremisesResourceCommand(val premisesId: PremisesId, val owner: UserData)
