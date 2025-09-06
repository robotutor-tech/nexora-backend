package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ScheduleTriggerConfigRequest::class, name = "SCHEDULE"),
    JsonSubTypes.Type(value = VoiceConfigRequest::class, name = "VOICE")
)
sealed class TriggerConfigRequest(val type: String)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TimeConfigRequest::class, name = "TIME"),
    JsonSubTypes.Type(value = SunConfigRequest::class, name = "SUN")
)
sealed class ScheduleConfigRequest(type: String) : ConfigRequest(type)

data class ScheduleTriggerConfigRequest(
    val config: ScheduleConfigRequest, val repeat: List<DayOfWeek>
) : TriggerConfigRequest("SCHEDULE")

data class TimeConfigRequest(val time: String) : ScheduleConfigRequest("TIME")
data class SunConfigRequest(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleConfigRequest("SUN")
data class VoiceConfigRequest(val commands: List<String>) : TriggerConfigRequest("VOICE")
