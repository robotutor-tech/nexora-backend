package com.robotutor.nexora.audit.repositories

import com.robotutor.nexora.audit.models.Audit
import com.robotutor.nexora.audit.models.AuditId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : ReactiveCrudRepository<Audit, AuditId>
