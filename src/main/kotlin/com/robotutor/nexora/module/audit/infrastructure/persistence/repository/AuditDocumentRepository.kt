package com.robotutor.nexora.module.audit.infrastructure.persistence.repository

import com.robotutor.nexora.module.audit.infrastructure.persistence.document.AuditDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditDocumentRepository : ReactiveMongoRepository<AuditDocument, String>
