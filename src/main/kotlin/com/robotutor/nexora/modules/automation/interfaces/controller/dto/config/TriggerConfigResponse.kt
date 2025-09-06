package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config

import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

sealed interface ScheduleConfigResponse : ConfigResponse

data class ScheduleTriggerConfigResponse(
    val config: ScheduleConfigResponse, val repeat: List<DayOfWeek>
) : TriggerConfigResponse

data class TimeConfigResponse(val time: String) : ScheduleConfigResponse

data class SunConfigResponse(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleConfigResponse

data class VoiceConfigResponse(val commands: List<String>) : TriggerConfigResponse

