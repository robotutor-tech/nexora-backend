package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.ConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.ConfigResponse

object ConfigMapper {
    fun toConfig(config: ConfigRequest): Config {
        val mapper = getMapper(config.type)
        return mapper.toConfig(config)
    }

    fun toConfigResponse(config: Config): ConfigResponse {
        val mapper = getMapper(config.type)
        return mapper.toConfigResponse(config)
    }

    private fun getMapper(type: ConfigType): Mapper<Config, ConfigResponse, ConfigRequest> {
        @Suppress("UNCHECKED_CAST")
        return when (type) {
            ConfigType.AUTOMATION -> AutomationMapper
            ConfigType.FEED_VALUE -> FeedValueMapper
            ConfigType.FEED_CONTROL -> FeedControlMapper
            ConfigType.SCHEDULE -> ScheduleMapper
            ConfigType.TIME_RANGE -> TimeRangeMapper
            ConfigType.VOICE -> VoiceMapper
            ConfigType.WAIT -> WaitMapper
        } as Mapper<Config, ConfigResponse, ConfigRequest>
    }
}