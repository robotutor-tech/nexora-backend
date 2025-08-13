package com.robotutor.nexora.modules.audit.adapters.persistence.repository

import com.robotutor.nexora.modules.audit.adapters.persistence.model.AuditDocument
import com.robotutor.nexora.modules.audit.domain.model.Audit
import com.robotutor.nexora.modules.audit.domain.repository.AuditRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AuditDocumentRepository : ReactiveCrudRepository<AuditDocument, String>

@Repository
class MongoAuditRepository(private val auditDocumentRepository: AuditDocumentRepository) : AuditRepository {
    override fun save(audit: Audit): Mono<Audit> {
        return auditDocumentRepository.save(AuditDocument.from(audit))
            .map { it.toDomainModel() }
    }
}
