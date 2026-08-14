package com.robotutor.nexora.module.identity.infrastructure.persistence.repository

import com.robotutor.nexora.module.identity.infrastructure.persistence.document.SessionDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface SessionDocumentRepository : ReactiveCrudRepository<SessionDocument, String> {
    fun findBySessionIdAndExpiresAtAfter(sessionId: String, expiresAt: Instant = Instant.now()): Mono<SessionDocument>
}
