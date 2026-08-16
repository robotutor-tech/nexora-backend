package com.robotutor.nexora.module.audit.application.service

import com.robotutor.nexora.module.audit.application.service.command.AddAuditCommand
import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import com.robotutor.nexora.module.audit.domain.repository.AuditRepository
import com.robotutor.nexora.module.audit.domain.vo.AuditId
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.application.logger.logOnError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AddAuditService(private val auditRepository: AuditRepository) {

    private val logger = Logger(AddAuditService::class.java)

    fun execute(command: AddAuditCommand): Mono<Audit> {
        val audit = Audit(
            auditId = AuditId.generate(),
            eventId = command.eventId,
            action = command.action,
            resource = command.resource,
            state = command.state,
            metadata = command.metadata,
            occurredAt = command.occurredAt,
            principalId = command.principalId,
            principalType = command.principalType,
            principalData = command.principalData,
        )
        return auditRepository.save(audit)
            .logOnSuccess(logger, "Successfully saved Audit.")
            .logOnError(logger, "Error while saving Audit.")
    }

}
