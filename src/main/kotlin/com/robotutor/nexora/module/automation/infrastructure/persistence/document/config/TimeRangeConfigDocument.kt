package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config

data class TimeRangeConfigDocument(val startTime: String, val endTime: String) : ConfigDocument(ConfigType.TIME_RANGE)
