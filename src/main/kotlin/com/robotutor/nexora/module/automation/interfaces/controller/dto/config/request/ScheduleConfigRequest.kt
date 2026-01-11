package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.module.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TimeConfigRequest::class, name = "TIME"),
    JsonSubTypes.Type(value = SunConfigRequest::class, name = "SUN")
)
sealed class ScheduleTypeConfigRequest(val type: String)

data class ScheduleTriggerConfigRequest(
    val config: ScheduleTypeConfigRequest,
    val repeat: List<DayOfWeek>
) : ConfigRequest(ConfigType.SCHEDULE)

data class TimeConfigRequest(val time: String) : ScheduleTypeConfigRequest("TIME")
data class SunConfigRequest(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleTypeConfigRequest("SUN")
