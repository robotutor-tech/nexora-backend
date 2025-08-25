//package com.robotutor.nexora.modules.audit.application
//
//import com.robotutor.nexora.modules.audit.application.command.CreateAuditCommand
//import com.robotutor.nexora.modules.audit.domain.model.Audit
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.domain.event.AuditEvent
//import com.robotutor.nexora.shared.domain.event.EventHandler
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class AuditEventHandler(private val saveAuditMessageUseCase: SaveAuditMessageUseCase) :
//    EventHandler<AuditEvent>(AuditEvent::class.java) {
//    private val logger = Logger(this::class.java)
//
//    override fun handle(event: AuditEvent): Mono<Any> {
//        val auditCommand = CreateAuditCommand(
//            event = event.event,
//            status = event.status,
//            metadata = event.metadata,
//            timestamp = event.occurredOn
//        )
//        return saveAuditMessageUseCase.addAudit(auditCommand)
//            .logOnSuccess(logger, "Successfully handled audit event: ${event.id}")
//            .logOnError(logger, "", "Failed to handle audit event: ${event.id}")
//            .map { event }
//    }
//}