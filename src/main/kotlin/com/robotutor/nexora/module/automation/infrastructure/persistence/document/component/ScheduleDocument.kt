//package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config
//
//import com.robotutor.nexora.module.automation.domain.entity.config.SunEvent
//import java.time.DayOfWeek
//
//sealed interface ScheduleTypeConfigDocument
//
//data class ScheduleComponentDocument(
//    val config: ScheduleTypeConfigDocument,
//    val repeat: List<DayOfWeek>
//) : ComponentDocument(ConfigType.SCHEDULE)
//
//data class TimeConfigDocument(
//    val time: String
//) : ScheduleTypeConfigDocument
//
//data class SunConfigDocument(
//    val event: SunEvent,
//    val offsetMinutes: Int = 0
//) : ScheduleTypeConfigDocument
//
