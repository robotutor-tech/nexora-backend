package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class TimeRangeConfigResponse(
    val startTime: String,
    val endTime: String
) : ConfigResponse(ConfigType.TIME_RANGE)