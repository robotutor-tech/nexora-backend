package com.robotutor.nexora.modules.audit.domain.repository

import com.robotutor.nexora.modules.audit.domain.model.Audit
import reactor.core.publisher.Mono

interface AuditRepository {
    fun save(audit: Audit): Mono<Audit>
}