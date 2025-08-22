package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.RoleDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleDocumentRepository : ReactiveMongoRepository<RoleDocument, String> {
    fun findAllByRoleIdIn(roles: List<String>): Flux<RoleDocument>
    fun findByRoleId(roleId: String): Mono<RoleDocument>
}