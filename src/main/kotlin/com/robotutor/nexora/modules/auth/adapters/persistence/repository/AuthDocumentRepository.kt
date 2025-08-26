package com.robotutor.nexora.modules.auth.adapters.persistence.repository

import com.robotutor.nexora.modules.auth.adapters.persistence.model.AuthUserDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AuthDocumentRepository : ReactiveCrudRepository<AuthUserDocument, String> {
    fun findByEmail(value: String): Mono<AuthUserDocument>
}