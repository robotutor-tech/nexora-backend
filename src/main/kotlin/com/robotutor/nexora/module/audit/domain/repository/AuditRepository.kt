package com.robotutor.nexora.module.audit.domain.repository

import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import reactor.core.publisher.Mono

interface AuditRepository {
    fun save(audit: Audit): Mono<Audit>
}
