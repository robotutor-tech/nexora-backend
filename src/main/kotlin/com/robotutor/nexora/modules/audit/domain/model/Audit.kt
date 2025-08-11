package com.robotutor.nexora.modules.audit.domain.model

data class Audit(
    val auditId: AuditId,
    val details: AuditDetails,
)
