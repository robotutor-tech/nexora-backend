package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class TimeRangeConfigDocument(val startTime: String, val endTime: String) : ConfigDocument(ConfigType.TIME_RANGE)
