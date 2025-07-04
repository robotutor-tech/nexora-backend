package com.robotutor.nexora.automation.models

import com.robotutor.nexora.automation.controllers.views.ConditionRequest
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val CONDITION_COLLECTION = "conditions"

@TypeAlias("Condition")
@Document(CONDITION_COLLECTION)
data class Condition(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val conditionId: ConditionId,
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val type: ConditionType,
    val config: ConditionConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(conditionId: ConditionId, request: ConditionRequest, premisesActorData: PremisesActorData): Condition {
            return Condition(
                conditionId = conditionId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                type = request.type,
                config = request.config,
            )
        }
    }
}

enum class ConditionType {
    TIME_RANGE,
    FEED,
}

sealed interface ConditionConfig

data class TimeRangeConditionConfig(val startTime: String, val endTime: String) : ConditionConfig

data class FeedConditionConfig(
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Double,
) : ConditionConfig


typealias ConditionId = String
