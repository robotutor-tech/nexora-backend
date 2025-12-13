package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.repository.GroupRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.GroupDocumentMapper
import com.robotutor.nexora.context.iam.infrastructure.persistence.repository.GroupDocumentRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoGroupRepository(
    private val groupDocumentRepository: GroupDocumentRepository,
    private val eventPublisher: EventPublisher<IAMDomainEvent>,
) : GroupRepository {
    override fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate> {
        val groupDocument = GroupDocumentMapper.toMongoDocument(groupAggregate)
        return groupDocumentRepository.save(groupDocument)
            .retryOptimisticLockingFailure()
            .map { GroupDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, groupAggregate)
    }
}