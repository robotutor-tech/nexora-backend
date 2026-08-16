package com.robotutor.nexora.module.audit.infrastructure.persistence

import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import com.robotutor.nexora.module.audit.domain.repository.AuditRepository
import com.robotutor.nexora.module.audit.infrastructure.persistence.mapper.AuditDocumentMapper
import com.robotutor.nexora.module.audit.infrastructure.persistence.repository.AuditDocumentRepository
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAuditRepository(private val auditDocumentRepository: AuditDocumentRepository) : AuditRepository {

    override fun save(audit: Audit): Mono<Audit> {
        val document = AuditDocumentMapper.toMongoDocument(audit)
        return auditDocumentRepository.save(document)
            .retryOptimisticLockingFailure()
            .map { AuditDocumentMapper.toDomainModel(it) }
    }
}
