package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.RoleAggregate
import com.robotutor.nexora.module.identity.domain.repository.RoleRepository
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.module.identity.infrastructure.messaging.mapper.IdentityEventMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.RoleDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.RoleDocumentRepository
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoRoleRepository(
    private val roleDocumentRepository: RoleDocumentRepository,
) : RoleRepository {
    override fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate> {
        val roleDocument = RoleDocumentMapper.toMongoDocument(roleAggregate)
        return roleDocumentRepository.save(roleDocument)
            .retryOptimisticLockingFailure()
            .map { RoleDocumentMapper.toDomainModel(it) }
            .publishEvents( roleAggregate, IdentityEventMapper)
    }

    override fun saveAll(roleAggregates: List<RoleAggregate>): Flux<RoleAggregate> {
        val roleDocuments = roleAggregates.map { roleAggregate -> RoleDocumentMapper.toMongoDocument(roleAggregate) }
        return roleDocumentRepository.saveAll(roleDocuments)
            .retryOptimisticLockingFailure()
            .map { RoleDocumentMapper.toDomainModel(it) }
//            .publishEvents(eventPublisher, roleAggregates)
    }

    override fun findAllByRoleIds(roleIds: Set<RoleId>): Flux<RoleAggregate> {
        return roleDocumentRepository.findAllByRoleIdIn(roleIds.map { it.value })
            .map { RoleDocumentMapper.toDomainModel(it) }
    }
}
