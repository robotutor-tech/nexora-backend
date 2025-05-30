package com.robotutor.nexora.audit.services

import com.robotutor.nexora.audit.models.Audit
import com.robotutor.nexora.audit.models.IdType
import com.robotutor.nexora.audit.repositories.AuditRepository
import com.robotutor.nexora.kafka.models.AuditMessage
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuditService(private val auditRepository: AuditRepository, private val idGeneratorService: IdGeneratorService) {

    val logger = Logger(this::class.java)

    fun addAudit(message: AuditMessage): Mono<Audit> {
        return idGeneratorService.generateId(IdType.AUDIT_ID)
            .map { auditId -> Audit.from(auditId, message) }
            .flatMap { auditRepository.save(it) }
            .retryOptimisticLockingFailure()
            .logOnSuccess(logger, "Successfully added audit message")
            .logOnError(logger, "", "Failed to add audit message")
    }
}