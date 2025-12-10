package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.SessionDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface SessionDocumentRepository : ReactiveCrudRepository<SessionDocument, String> {
    fun findByRefreshTokenHashAndExpiresAtAfter(
        refreshToken: String,
        expiresAt: Instant = Instant.now()
    ): Mono<SessionDocument>
}