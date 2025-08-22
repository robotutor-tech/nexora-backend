//package com.robotutor.nexora.modules.automation.models
//
//import com.robotutor.nexora.modules.feed.models.FeedId
//import com.robotutor.nexora.common.security.models.PremisesId
//import org.bson.types.ObjectId
//import java.time.DayOfWeek
//import java.time.Instant
//
//data class Trigger(
//    val id: ObjectId? = null,
//    val triggerId: TriggerId,
//    val premisesId: PremisesId,
//    val name: String,
//    val description: String? = null,
//    val config: TriggerConfig,
//    val createdOn: Instant = Instant.now(),
//    val updatedOn: Instant = Instant.now(),
//    val version: Long? = null,
//)
//
//enum class TriggerType {
//    SCHEDULE,
//    VOICE,
//    FEED
//}
//
//sealed interface TriggerConfig {
//    val type: TriggerType
//}
//
//sealed interface ScheduleConfig {
//    val type: ScheduleType
//}
//
//data class ScheduleTriggerConfig(
//    override val type: TriggerType = TriggerType.SCHEDULE,
//    val config: ScheduleConfig,
//    val repeat: List<DayOfWeek>
//) : TriggerConfig
//
//data class TimeTriggerConfig(
//    override val type: ScheduleType = ScheduleType.TIME,
//    val time: String,
//) : ScheduleConfig
//
//data class SunTriggerConfig(
//    override val type: ScheduleType = ScheduleType.SUN,
//    val event: SunEvent,
//    val offsetMinutes: Int = 0
//) : ScheduleConfig
//
//data class VoiceTriggerConfig(
//    override val type: TriggerType = TriggerType.VOICE,
//    var commands: List<String>,
//) : TriggerConfig {
//
//    fun sanitizeCommands() {
//        commands = commands.map { command -> command.split(" ").filter { c -> c.isNotBlank() }.joinToString(" ") }
//            .toSet().toList()
//    }
//}
//
//data class FeedTriggerConfig(
//    override val type: TriggerType = TriggerType.FEED,
//    val feedId: FeedId,
//    val operator: ComparisonOperator,
//    val value: Double,
//    val isTriggered: Boolean = false,
//) : TriggerConfig
//
//enum class ScheduleType {
//    SUN, TIME
//}
//
//enum class SunEvent {
//    SUNRISE, SUNSET
//}
//
//enum class ComparisonOperator {
//    GREATER_THAN, LESS_THAN, EQUAL, NOT_EQUAL, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
//}
//
//typealias TriggerId = String
