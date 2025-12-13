package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.repository.RoleRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.RoleDocumentMapper
import com.robotutor.nexora.context.iam.infrastructure.persistence.repository.RoleDocumentRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoRoleRepository(
    private val roleDocumentRepository: RoleDocumentRepository,
    private val eventPublisher: EventPublisher<IAMDomainEvent>,
) : RoleRepository {
    override fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate> {
        val roleDocument = RoleDocumentMapper.toMongoDocument(roleAggregate)
        return roleDocumentRepository.save(roleDocument)
            .retryOptimisticLockingFailure()
            .map { RoleDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, roleAggregate)
    }
}