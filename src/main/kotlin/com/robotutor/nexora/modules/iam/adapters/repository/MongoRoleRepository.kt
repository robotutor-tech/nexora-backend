package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.RoleDocument
import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.modules.iam.domain.repository.RoleRepository
import com.robotutor.nexora.shared.domain.model.RoleId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoRoleRepository(private val roleDocumentRepository: RoleDocumentRepository) : RoleRepository {
    override fun save(role: Role): Mono<Role> {
        return roleDocumentRepository.save(RoleDocument.from(role))
            .map { it.toDomainModel() }
    }

    override fun findAllByRoleIdIn(roles: List<RoleId>): Flux<Role> {
        return roleDocumentRepository.findAllByRoleIdIn(roles.map { it.value })
            .map { it.toDomainModel() }
    }

    override fun findByRoleId(roleId: RoleId): Mono<Role> {
        return roleDocumentRepository.findByRoleId(roleId = roleId.value)
            .map { it.toDomainModel() }
    }
}