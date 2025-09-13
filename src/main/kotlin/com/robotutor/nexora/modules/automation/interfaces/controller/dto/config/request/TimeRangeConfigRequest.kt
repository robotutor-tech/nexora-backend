package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class TimeRangeConfigRequest(val startTime: String, val endTime: String) : ConfigRequest(ConfigType.TIME_RANGE)