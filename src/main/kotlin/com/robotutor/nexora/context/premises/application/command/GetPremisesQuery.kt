package com.robotutor.nexora.context.premises.application.command

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class GetPremisesQuery(val premisesIds: List<PremisesId>)
