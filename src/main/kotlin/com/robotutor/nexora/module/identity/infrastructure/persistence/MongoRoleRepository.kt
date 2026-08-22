package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.Role
import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.repository.RoleRepository
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.RoleDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.RoleDocumentRepository
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoRoleRepository(
    private val roleDocumentRepository: RoleDocumentRepository,
    private val eventMapper: EventMapper<IdentityEvent>,
) : RoleRepository {
    override fun save(role: Role): Mono<Role> {
        val roleDocument = RoleDocumentMapper.toDocument(role)
        return roleDocumentRepository.save(roleDocument)
            .retryOptimisticLockingFailure()
            .map { RoleDocumentMapper.toDomain(it) }
            .publishEvents(role, eventMapper)
    }

    override fun saveAll(roles: List<Role>): Flux<Role> {
        val roleDocuments = roles.map { roleAggregate -> RoleDocumentMapper.toDocument(roleAggregate) }
        return roleDocumentRepository.saveAll(roleDocuments)
            .retryOptimisticLockingFailure()
            .map { RoleDocumentMapper.toDomain(it) }
//            .publishEvents(eventPublisher, roleAggregates)
    }

    override fun findAllByRoleIds(roleIds: Set<RoleId>): Flux<Role> {
        return roleDocumentRepository.findAllByRoleIdIn(roleIds.map { it.value })
            .map { RoleDocumentMapper.toDomain(it) }
    }
}
