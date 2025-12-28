package com.robotutor.nexora.shared.application.reactive

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class TraceData(
    val correlationId: String,
    val premisesId: PremisesId
)
