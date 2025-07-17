package com.robotutor.nexora.automation.models

import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import java.time.Instant

data class Condition(
    var id: ObjectId? = null,
    val conditionId: ConditionId,
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val config: ConditionConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null
)

enum class ConditionType {
    TIME_RANGE,
    FEED,
}

sealed interface ConditionConfig {
    val type: ConditionType
}

data class TimeRangeConditionConfig(
    override val type: ConditionType = ConditionType.TIME_RANGE,
    val startTime: String,
    val endTime: String
) : ConditionConfig

data class FeedConditionConfig(
    override val type: ConditionType = ConditionType.FEED,
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Double,
) : ConditionConfig


typealias ConditionId = String
