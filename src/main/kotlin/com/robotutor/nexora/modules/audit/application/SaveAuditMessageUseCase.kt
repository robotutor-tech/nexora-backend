//package com.robotutor.nexora.modules.audit.application
//
//import com.robotutor.nexora.modules.audit.application.command.CreateAuditCommand
//import com.robotutor.nexora.modules.audit.domain.model.Audit
//import com.robotutor.nexora.modules.audit.domain.model.AuditId
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import com.robotutor.nexora.modules.audit.domain.model.IdType
//import com.robotutor.nexora.modules.audit.domain.repository.AuditRepository
//import com.robotutor.nexora.shared.domain.service.IdGeneratorService
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class SaveAuditMessageUseCase(
//    private val auditRepository: AuditRepository,
//    private val idGeneratorService: IdGeneratorService
//) {
//    private val logger = Logger(this::class.java)
//
//    fun addAudit(auditCommand: CreateAuditCommand): Mono<Audit> {
//        return this.idGeneratorService.generateId(IdType.AUDIT_ID)
//            .map { auditId ->
//                Audit(
//                    auditId = AuditId(auditId),
//                    event = auditCommand.event,
//                    actorId = auditCommand.actorId,
//                    identifier = auditCommand.identifier,
//                    premisesId = auditCommand.premisesId,
//                    status = auditCommand.status,
//                    metadata = auditCommand.metadata,
//                )
//            }
//            .flatMap { audit -> this.auditRepository.save(audit) }
//            .logOnSuccess(logger, "Successfully added audit message")
//            .logOnError(logger, "", "Failed to add audit message")
//    }
//}