package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.GroupAggregate
import com.robotutor.nexora.module.identity.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.identity.domain.repository.GroupRepository
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.GroupDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.GroupDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoGroupRepository(
    private val groupDocumentRepository: GroupDocumentRepository,
    private val eventPublisher: IAMEventPublisher,
) : GroupRepository {
    override fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate> {
        val groupDocument = GroupDocumentMapper.toMongoDocument(groupAggregate)
        return groupDocumentRepository.save(groupDocument)
            .retryOptimisticLockingFailure()
            .map { GroupDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, groupAggregate)
    }

    override fun saveAll(groupAggregates: List<GroupAggregate>): Flux<GroupAggregate> {
        val groupDocuments = groupAggregates.map { GroupDocumentMapper.toMongoDocument(it) }
        return groupDocumentRepository.saveAll(groupDocuments)
            .retryOptimisticLockingFailure()
            .map { GroupDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, groupAggregates)
    }

    override fun findAllByGroupIds(groupIds: Set<GroupId>): Flux<GroupAggregate> {
        return groupDocumentRepository.findAllByGroupIdIn(groupIds.map { it.value })
            .map { GroupDocumentMapper.toDomainModel(it) }
    }
}
