package com.robotutor.nexora.module.feed.infrastructure.persistence

import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.module.feed.domain.repository.FeedRepository
import com.robotutor.nexora.module.feed.infrastructure.persistence.document.FeedDocument
import com.robotutor.nexora.module.feed.infrastructure.persistence.mapper.FeedDocumentMapper
import com.robotutor.nexora.module.feed.infrastructure.persistence.mapper.FeedSpecificationTranslator
import com.robotutor.nexora.module.feed.infrastructure.persistence.repository.FeedDocumentRepository
import com.robotutor.nexora.shared.domain.specification.Specification
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class MongoFeedRepository(
    private val feedDocumentRepository: FeedDocumentRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : FeedRepository {
    override fun saveAll(feeds: List<FeedAggregate>): Flux<FeedAggregate> {
        val documents = feeds.map { FeedDocumentMapper.toMongoDocument(it) }
        return feedDocumentRepository.saveAll(documents)
            .map { FeedDocumentMapper.toDomainModel(it) }
    }

    override fun findAll(specification: Specification<FeedAggregate>): Flux<FeedAggregate> {
        val query = Query(FeedSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<FeedDocument>(query)
            .map { FeedDocumentMapper.toDomainModel(it) }
    }
}

