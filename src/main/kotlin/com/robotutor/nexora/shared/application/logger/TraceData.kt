package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class TraceData(
    val correlationId: String,
    val premisesId: PremisesId
)