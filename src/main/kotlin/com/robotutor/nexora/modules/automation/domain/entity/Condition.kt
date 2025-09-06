package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.domain.entity.config.ConditionConfig
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import java.time.Instant

data class Condition(
    val conditionId: ConditionId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val config: ConditionConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null
)

enum class ConditionType {
    TIME_RANGE,
    FEED_CONTROL,
}


data class ConditionId(val value: String)
