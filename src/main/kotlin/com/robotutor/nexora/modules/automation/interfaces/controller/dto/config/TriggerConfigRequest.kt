package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config

import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

sealed interface ScheduleConfigRequest : ConfigRequest

data class ScheduleTriggerConfigRequest(
    val config: ScheduleConfigRequest, val repeat: List<DayOfWeek>
) : TriggerConfigRequest

data class TimeConfigRequest(val time: String) : ScheduleConfigRequest

data class SunConfigRequest(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleConfigRequest

data class VoiceConfigRequest(val commands: List<String>) : TriggerConfigRequest

