package com.robotutor.nexora.context.premises.application.command

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class GetAllPremisesQuery(val premisesIds: List<PremisesId>)
data class GetPremisesQuery(val premisesId: PremisesId)
