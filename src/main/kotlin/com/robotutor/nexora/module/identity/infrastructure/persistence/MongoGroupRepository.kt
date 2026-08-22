package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.Group
import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.repository.GroupRepository
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.GroupDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.GroupDocumentRepository
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoGroupRepository(
    private val groupDocumentRepository: GroupDocumentRepository,
    private val eventMapper: EventMapper<IdentityEvent>,
) : GroupRepository {
    override fun save(group: Group): Mono<Group> {
        val groupDocument = GroupDocumentMapper.toDocument(group)
        return groupDocumentRepository.save(groupDocument)
            .retryOptimisticLockingFailure()
            .map { GroupDocumentMapper.toDomain(it) }
            .publishEvents(group, eventMapper)
    }

    override fun saveAll(groups: List<Group>): Flux<Group> {
        val groupDocuments = groups.map { GroupDocumentMapper.toDocument(it) }
        return groupDocumentRepository.saveAll(groupDocuments)
            .retryOptimisticLockingFailure()
            .map { GroupDocumentMapper.toDomain(it) }
//            .publishEvents(eventPublisher, groupAggregates)
    }

    override fun findAllByGroupIds(groupIds: Set<GroupId>): Flux<Group> {
        return groupDocumentRepository.findAllByGroupIdIn(groupIds.map { it.value })
            .map { GroupDocumentMapper.toDomain(it) }
    }
}
