package com.robotutor.nexora.modules.premises.domain.model

import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class Premises(
    val premisesId: PremisesId,
    val name: String,
    val owner: UserId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
)

