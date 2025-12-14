package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.RoleDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RoleDocumentRepository : ReactiveCrudRepository<RoleDocument, String>{
    fun findAllByRoleIdIn(roleIds: List<String>): Flux<RoleDocument>
}