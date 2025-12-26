package com.robotutor.nexora.context.device.infrastructure.persistence

import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.context.device.domain.repository.FeedRepository
import com.robotutor.nexora.context.device.infrastructure.persistence.document.FeedDocument
import com.robotutor.nexora.context.device.infrastructure.persistence.mapper.FeedDocumentMapper
import com.robotutor.nexora.context.device.infrastructure.persistence.mapper.FeedSpecificationTranslator
import com.robotutor.nexora.context.device.infrastructure.persistence.repository.FeedDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.Specification
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class MongoFeedRepository(
    private val feedDocumentRepository: FeedDocumentRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val eventPublisher: DeviceEventPublisher,
) : FeedRepository {
    override fun saveAll(feeds: List<FeedAggregate>): Flux<FeedAggregate> {
        val documents = feeds.map { FeedDocumentMapper.toMongoDocument(it) }
        return feedDocumentRepository.saveAll(documents)
            .map { FeedDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, feeds)
    }

    override fun findAll(specification: Specification<FeedAggregate>): Flux<FeedAggregate> {
        val query = Query(FeedSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<FeedDocument>(query)
            .map { FeedDocumentMapper.toDomainModel(it) }
    }
}