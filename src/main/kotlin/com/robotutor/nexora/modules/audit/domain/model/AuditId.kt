package com.robotutor.nexora.modules.audit.domain.model

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    AUDIT_ID(12);
}

data class AuditId(val value: String)
