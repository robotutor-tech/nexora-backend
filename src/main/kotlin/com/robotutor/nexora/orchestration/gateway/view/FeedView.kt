package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.orchestration.models.FeedType
import com.robotutor.nexora.premises.models.PremisesId

data class FeedView(
    val feedId: String,
    val premisesId: PremisesId,
    val name: String,
    val type: FeedType,
)
