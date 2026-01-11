package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.entity.config.ScheduleConfig
import com.robotutor.nexora.module.automation.domain.entity.config.TimeConfig
import com.robotutor.nexora.module.automation.domain.entity.config.SunConfig
import com.robotutor.nexora.module.automation.domain.entity.config.Time
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.ScheduleConfigDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.TimeConfigDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.SunConfigDocument

object ScheduleDocumentMapper : Mapper<ScheduleConfig, ScheduleConfigDocument> {
    override fun toDocument(config: ScheduleConfig): ScheduleConfigDocument {
        val doc = when (config.config) {
            is TimeConfig -> TimeConfigDocument(time = config.config.time.toTimeString())
            is SunConfig -> SunConfigDocument(event = config.config.event, offsetMinutes = config.config.offsetMinutes)
        }
        return ScheduleConfigDocument(config = doc, repeat = config.repeat)
    }

    override fun toDomain(doc: ScheduleConfigDocument): ScheduleConfig {
        val sub = when (doc.config) {
            is TimeConfigDocument -> TimeConfig(Time.from(doc.config.time))
            is SunConfigDocument -> SunConfig(event = doc.config.event, offsetMinutes = doc.config.offsetMinutes)
        }
        return ScheduleConfig(config = sub, repeat = doc.repeat)
    }
}

