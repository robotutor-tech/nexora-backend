package com.robotutor.nexora.shared.application.resolver

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class TraceData(
    val correlationId: String,
    val premisesId: PremisesId
)
