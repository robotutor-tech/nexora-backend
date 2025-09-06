package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.validation
import java.time.DayOfWeek

sealed interface ScheduleConfig : Config

data class ScheduleTriggerConfig(val config: ScheduleConfig, val repeat: List<DayOfWeek>) : TriggerConfig {
    init {
        validation(repeat.isNotEmpty()) { "Repeat must not be empty" }
    }
}

data class TimeConfig(val time: Time) : ScheduleConfig

data class SunConfig(val event: SunEvent, val offsetMinutes: Int = 0) : ScheduleConfig {
    init {
        validation(offsetMinutes in -60..60) { "Offset must be between -60 and 60 minutes" }
    }
}

data class VoiceConfig(val commands: VoiceCommands) : TriggerConfig

