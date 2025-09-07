package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.validation
import java.time.DayOfWeek

sealed class ScheduleSubConfig(val type: ScheduleSubConfigType)

data class ScheduleConfig(val config: ScheduleSubConfig, val repeat: List<DayOfWeek>) : TriggerConfig, RuleConfigType(ConfigType.SCHEDULE) {
    init {
        validation(repeat.isNotEmpty()) { "Repeat must not be empty" }
    }
}

data class TimeConfig(val time: Time) : ScheduleSubConfig(ScheduleSubConfigType.TIME)

data class SunConfig(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleSubConfig(ScheduleSubConfigType.SUN) {
    init {
        validation(offsetMinutes in -60..60) { "Offset must be between -60 and 60 minutes" }
    }
}

enum class SunEvent {
    SUNRISE, SUNSET
}

enum class ScheduleSubConfigType {
    TIME, SUN
}
