package com.robotutor.nexora.module.automation.infrastructure.persistence

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.infrastructure.messaging.mapper.AutomationEventMapper
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.AutomationDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.AutomationDocumentMapper
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.AutomationSpecificationTranslator
import com.robotutor.nexora.module.automation.infrastructure.persistence.repository.AutomationDocumentRepository
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.outbox.publishEvents
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoAutomationRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val automationDocumentRepository: AutomationDocumentRepository,

) : AutomationRepository {
    override fun save(automationAggregate: AutomationAggregate): Mono<AutomationAggregate> {
        val document = AutomationDocumentMapper.toDocument(automationAggregate)
        return automationDocumentRepository.save(document)
            .map { AutomationDocumentMapper.toDomain(it) }
            .publishEvents(automationAggregate, AutomationEventMapper)
    }

    override fun findAll(specification: Specification<AutomationAggregate>): Flux<AutomationAggregate> {
        val query = Query(AutomationSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<AutomationDocument>(query)
            .map { AutomationDocumentMapper.toDomain(it) }
    }

    override fun findByAutomationIdAndPremisesId(
        automationId: AutomationId,
        premisesId: PremisesId
    ): Mono<AutomationAggregate> {
        return automationDocumentRepository.findByAutomationIdAndPremisesId(automationId.value, premisesId.value)
            .map { AutomationDocumentMapper.toDomain(it) }
    }

    override fun findByAutomationId(automationId: AutomationId): Mono<AutomationAggregate> {
        return automationDocumentRepository.findByAutomationId(automationId.value)
            .map { AutomationDocumentMapper.toDomain(it) }
    }
}
