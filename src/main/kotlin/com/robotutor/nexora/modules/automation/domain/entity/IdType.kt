package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.common.persistence.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    AUTOMATION_ID(10),
    RULE_ID(12),
    EXECUTION_ID(16),
}