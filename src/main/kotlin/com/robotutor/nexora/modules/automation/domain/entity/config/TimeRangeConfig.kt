package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.utility.validation

data class TimeRangeConfig(val startTime: Time, val endTime: Time) : ConditionConfig, RuleConfigType(ConfigType.TIME_RANGE) {
    init {
        validation(startTime != endTime) { "Start time must not equal end time" }
    }
}