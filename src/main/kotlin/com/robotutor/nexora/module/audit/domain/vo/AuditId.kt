package com.robotutor.nexora.module.audit.domain.vo

import com.robotutor.nexora.shared.domain.vo.Identifier
import java.util.*

data class AuditId(override val value: String) : Identifier {
    companion object {
        fun generate(): AuditId {
            return AuditId(UUID.randomUUID().toString())
        }
    }
}
