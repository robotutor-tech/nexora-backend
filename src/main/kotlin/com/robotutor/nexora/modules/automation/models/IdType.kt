package com.robotutor.nexora.modules.automation.models

import com.robotutor.nexora.common.security.service.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    AUTOMATION_ID(10),
    TRIGGER_ID(12),
    CONDITION_ID(12),
    ACTION_ID(12),
    EXECUTION_ID(16),
}