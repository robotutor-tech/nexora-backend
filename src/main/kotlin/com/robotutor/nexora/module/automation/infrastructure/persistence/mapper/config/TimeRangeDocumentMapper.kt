package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.entity.config.Time
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.TimeRangeConfigDocument

object TimeRangeDocumentMapper : Mapper<TimeRangeConfig, TimeRangeConfigDocument>{
    override fun toDocument(config: TimeRangeConfig): TimeRangeConfigDocument {
        return TimeRangeConfigDocument(
            startTime = config.startTime.toTimeString(),
            endTime = config.endTime.toTimeString()
        )
    }

    override fun toDomain(doc: TimeRangeConfigDocument): TimeRangeConfig {
        return TimeRangeConfig(startTime = Time.from(doc.startTime), endTime = Time.from(doc.endTime))
    }
}

