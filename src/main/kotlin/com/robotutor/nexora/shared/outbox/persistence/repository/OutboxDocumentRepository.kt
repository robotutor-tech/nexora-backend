package com.robotutor.nexora.shared.outbox.persistence.repository

import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import com.robotutor.nexora.shared.outbox.persistence.document.Status
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface OutboxDocumentRepository : ReactiveCrudRepository<OutboxDocument, String> {
    fun findAllByStatus(status: Status): Flux<OutboxDocument>
}
