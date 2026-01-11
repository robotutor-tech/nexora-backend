package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

data class TimeRangeConfigRequest(val startTime: String, val endTime: String) : ConfigRequest(ConfigType.TIME_RANGE)