package com.robotutor.nexora.modules.audit.config

import com.robotutor.nexora.modules.audit.adapters.outbound.persistence.repository.MongoAuditRepository
import com.robotutor.nexora.modules.audit.application.SaveAuditMessageUseCase
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("AuditConfiguration")
class Configuration {
    @Bean
    fun saveAuditMessageUseCase(
        mongoAuditRepository: MongoAuditRepository,
        idGeneratorService: IdGeneratorService
    ): SaveAuditMessageUseCase {
        return SaveAuditMessageUseCase(auditRepository = mongoAuditRepository, idGeneratorService = idGeneratorService)
    }
}