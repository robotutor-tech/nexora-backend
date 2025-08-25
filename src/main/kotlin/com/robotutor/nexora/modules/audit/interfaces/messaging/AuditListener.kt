//package com.robotutor.nexora.modules.audit.interfaces.messaging
//
//import org.springframework.stereotype.Service
//
//@Service
//class AuditEventHandler(
//    private val saveAuditMessageUseCase: SaveAuditMessageUseCase,
//    private val kafkaConsumer: KafkaConsumer,
//) {
//
//    @PostConstruct
//    fun subscribe() {
//        kafkaConsumer.consume(listOf("AUDIT"), AuditMessage::class.java) {
//            saveAuditMessageUseCase.addAudit(AuditMapper.toCreateAuditCommand(it.message))
//        }
//            .subscribe()
//    }
//}
