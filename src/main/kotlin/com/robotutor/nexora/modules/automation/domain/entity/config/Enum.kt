package com.robotutor.nexora.modules.automation.domain.entity.config

enum class ScheduleType {
    SUN, TIME
}

enum class SunEvent {
    SUNRISE, SUNSET
}

enum class ComparisonOperator {
    GREATER_THAN, LESS_THAN, EQUAL, NOT_EQUAL, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
}
