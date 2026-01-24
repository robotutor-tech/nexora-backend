package com.robotutor.nexora.module.automation.infrastructure.persistence

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.event.AutomationEventPublisher
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.AutomationDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.AutomationDocumentMapper
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.AutomationSpecificationTranslator
import com.robotutor.nexora.module.automation.infrastructure.persistence.repository.AutomationDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.PremisesId
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
    private val eventPublisher: AutomationEventPublisher
) : AutomationRepository {
    override fun save(automationAggregate: AutomationAggregate): Mono<AutomationAggregate> {
        val document = AutomationDocumentMapper.toMongoDocument(automationAggregate)
        return automationDocumentRepository.save(document)
            .map { AutomationDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher)
    }

    override fun findAll(specification: Specification<AutomationAggregate>): Flux<AutomationAggregate> {
        val query = Query(AutomationSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<AutomationDocument>(query)
            .map { AutomationDocumentMapper.toDomainModel(it) }
    }

    override fun findByAutomationIdAndPremisesId(
        automationId: AutomationId,
        premisesId: PremisesId
    ): Mono<AutomationAggregate> {
        return automationDocumentRepository.findByAutomationIdAndPremisesId(automationId.value, premisesId.value)
            .map { AutomationDocumentMapper.toDomainModel(it) }
    }

    override fun findByAutomationId(automationId: AutomationId): Mono<AutomationAggregate> {
        return automationDocumentRepository.findByAutomationId(automationId.value)
            .map { AutomationDocumentMapper.toDomainModel(it) }
    }
}