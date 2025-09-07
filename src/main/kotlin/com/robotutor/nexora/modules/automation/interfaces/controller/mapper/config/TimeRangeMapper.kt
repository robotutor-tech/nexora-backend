package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.TimeRangeConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Time
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.TimeRangeConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.TimeRangeConfigResponse

object TimeRangeMapper : Mapper<TimeRangeConfig, TimeRangeConfigResponse, TimeRangeConfigRequest> {
    override fun toConfigResponse(config: TimeRangeConfig): TimeRangeConfigResponse {
        return TimeRangeConfigResponse(
            startTime = config.startTime.toTimeString(),
            endTime = config.endTime.toTimeString()
        )
    }

    override fun toConfig(request: TimeRangeConfigRequest): TimeRangeConfig {
        return TimeRangeConfig(
            startTime = Time.from(request.startTime),
            endTime = Time.from(request.endTime)
        )
    }
}

