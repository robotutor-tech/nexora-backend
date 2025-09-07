package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ScheduleConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.ScheduleSubConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.TimeConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.SunConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.ScheduleTriggerConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.TimeConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.SunConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.ScheduleConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.TimeConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.SunConfigResponse
import com.robotutor.nexora.modules.automation.domain.entity.config.Time

object ScheduleMapper : Mapper<ScheduleConfig, ScheduleConfigResponse, ScheduleTriggerConfigRequest> {
    override fun toConfigResponse(config: ScheduleConfig): ScheduleConfigResponse {
        val sub = when (val c = config.config) {
            is TimeConfig -> TimeConfigResponse(time = c.time.toTimeString())
            is SunConfig -> SunConfigResponse(event = c.event, offsetMinutes = c.offsetMinutes)
        }
        return ScheduleConfigResponse(config = sub, repeat = config.repeat)
    }

    override fun toConfig(request: ScheduleTriggerConfigRequest): ScheduleConfig {
        val sub: ScheduleSubConfig = when (val rc = request.config) {
            is TimeConfigRequest -> TimeConfig(Time.from(rc.time))
            is SunConfigRequest -> SunConfig(event = rc.event, offsetMinutes = rc.offsetMinutes)
        }
        return ScheduleConfig(config = sub, repeat = request.repeat)
    }
}
