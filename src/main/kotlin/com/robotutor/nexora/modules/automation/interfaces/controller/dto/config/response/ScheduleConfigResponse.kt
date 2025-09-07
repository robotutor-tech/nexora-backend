package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType
import com.robotutor.nexora.modules.automation.domain.entity.config.ScheduleSubConfigType
import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek


data class ScheduleConfigResponse(
    val config: ScheduleSubConfigResponse,
    val repeat: List<DayOfWeek>
) : ConfigResponse(ConfigType.SCHEDULE)


sealed class ScheduleSubConfigResponse(
    val type: ScheduleSubConfigType
)

data class TimeConfigResponse(
    val time: String
) : ScheduleSubConfigResponse(ScheduleSubConfigType.TIME)

data class SunConfigResponse(
    val event: SunEvent,
    val offsetMinutes: Int = 0
) : ScheduleSubConfigResponse(ScheduleSubConfigType.SUN)

