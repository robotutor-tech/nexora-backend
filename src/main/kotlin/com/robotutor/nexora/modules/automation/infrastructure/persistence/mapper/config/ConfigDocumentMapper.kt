package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ConfigDocument

object ConfigDocumentMapper {

    fun toConfigDocument(config: Config): ConfigDocument {
        val mapper = getMapper(config.type)
        return mapper.toDocument(config)
    }

    fun toConfig(config: ConfigDocument): Config {
        val mapper = getMapper(config.type)
        return mapper.toDomain(config)
    }

    private fun getMapper(type: ConfigType): Mapper<Config, ConfigDocument> {
        @Suppress("UNCHECKED_CAST")
        return when (type) {
            ConfigType.AUTOMATION -> AutomationDocumentMapper
            ConfigType.FEED_VALUE -> FeedValueDocumentMapper
            ConfigType.FEED_CONTROL -> FeedControlDocumentMapper
            ConfigType.SCHEDULE -> ScheduleDocumentMapper
            ConfigType.TIME_RANGE -> TimeRangeDocumentMapper
            ConfigType.VOICE -> VoiceDocumentMapper
            ConfigType.WAIT -> WaitDocumentMapper
        } as Mapper<Config, ConfigDocument>
    }
}