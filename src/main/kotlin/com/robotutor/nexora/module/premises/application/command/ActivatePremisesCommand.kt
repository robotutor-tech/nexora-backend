package com.robotutor.nexora.module.premises.application.command

import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class ActivatePremisesCommand(val premisesId: PremisesId, val ownerId: AccountId)
