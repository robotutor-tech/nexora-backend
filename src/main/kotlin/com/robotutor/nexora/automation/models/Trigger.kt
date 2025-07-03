package com.robotutor.nexora.automation.models

import com.robotutor.nexora.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.DayOfWeek
import java.time.Instant


const val TRIGGER_COLLECTION = "triggers"

@TypeAlias("Trigger")
@Document(TRIGGER_COLLECTION)
data class Trigger(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val triggerId: TriggerId,
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val type: TriggerType,
    val config: TriggerConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(triggerId: TriggerId, request: TriggerRequest, premisesActorData: PremisesActorData): Trigger {
            return Trigger(
                triggerId = triggerId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                type = request.type,
                config = request.config,
            )
        }
    }
}

enum class TriggerType {
    SCHEDULE,
    VOICE,
    FEED
}

sealed interface TriggerConfig
sealed interface ScheduleConfig

data class ScheduleTriggerConfig(
    val type: ScheduleType,
    val config: ScheduleConfig,
    val repeat: List<DayOfWeek>
) : TriggerConfig

data class TimeTriggerConfig(
    val time: String,
) : ScheduleConfig

data class SunTriggerConfig(
    val event: SunEvent,
    val offsetMinutes: Int = 0
) : ScheduleConfig

data class VoiceTriggerConfig(
    val commands: List<String>,
) : TriggerConfig

data class FeedTriggerConfig(
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Double,
    val isTriggered: Boolean = false,
) : TriggerConfig

enum class ScheduleType {
    SUN, TIME
}

enum class SunEvent {
    SUNRISE, SUNSET
}

enum class ComparisonOperator {
    GREATER_THAN, LESS_THAN, EQUAL, NOT_EQUAL, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
}

typealias TriggerId = String
