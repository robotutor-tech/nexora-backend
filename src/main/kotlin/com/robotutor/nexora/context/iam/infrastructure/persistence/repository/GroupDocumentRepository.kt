package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.GroupDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface GroupDocumentRepository : ReactiveCrudRepository<GroupDocument, String> {
    fun findAllByGroupIdIn(groupIds: List<String>): Flux<GroupDocument>
}