package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserData

data class RegisterPremisesResourceCommand(val premisesId: PremisesId, val owner: UserData)