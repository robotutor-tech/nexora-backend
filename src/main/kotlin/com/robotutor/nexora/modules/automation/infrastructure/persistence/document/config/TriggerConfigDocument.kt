package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

sealed interface ScheduleConfigDocument : ConfigDocument

data class ScheduleTriggerConfigDocument(
    val config: ScheduleConfigDocument,
    val repeat: List<DayOfWeek>
) : TriggerConfigDocument

data class TimeTriggerConfigDocument(
    val time: String
) : ScheduleConfigDocument

data class SunTriggerConfigDocument(
    val event: SunEvent,
    val offsetMinutes: Int = 0
) : ScheduleConfigDocument

data class VoiceTriggerConfigDocument(val commands: List<String>) : TriggerConfigDocument
