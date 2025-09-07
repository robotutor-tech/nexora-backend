package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.SunEvent
import java.time.DayOfWeek

sealed interface ScheduleTypeConfigDocument

data class ScheduleConfigDocument(
    val config: ScheduleTypeConfigDocument,
    val repeat: List<DayOfWeek>
) : ConfigDocument

data class TimeConfigDocument(
    val time: String
) : ScheduleTypeConfigDocument

data class SunConfigDocument(
    val event: SunEvent,
    val offsetMinutes: Int = 0
) : ScheduleTypeConfigDocument

