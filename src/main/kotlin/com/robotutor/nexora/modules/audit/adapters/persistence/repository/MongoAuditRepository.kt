package com.robotutor.nexora.modules.audit.adapters.persistence.repository

import com.robotutor.nexora.modules.audit.adapters.persistence.mapper.AuditDocumentMapper
import com.robotutor.nexora.modules.audit.adapters.persistence.model.AuditDocument
import com.robotutor.nexora.modules.audit.domain.model.Audit
import com.robotutor.nexora.modules.audit.domain.repository.AuditRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAuditRepository(mongoTemplate: ReactiveMongoTemplate) :
    MongoRepository<Audit, AuditDocument>(mongoTemplate, AuditDocument::class.java, AuditDocumentMapper()),
    AuditRepository {

    override fun save(audit: Audit): Mono<Audit> {
        val query = Query(Criteria.where("auditId").`is`(audit.auditId.value))
        return this.findAndReplace(query, audit)
    }
}