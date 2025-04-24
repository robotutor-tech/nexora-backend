package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.premises.models.PremisesId

data class PremisesView(
    val premisesId: PremisesId,
    val name: String,
)
