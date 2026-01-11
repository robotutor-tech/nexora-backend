package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response

data class TimeRangeConfigResponse(
    val startTime: String,
    val endTime: String
) : ConfigResponse(ConfigType.TIME_RANGE)