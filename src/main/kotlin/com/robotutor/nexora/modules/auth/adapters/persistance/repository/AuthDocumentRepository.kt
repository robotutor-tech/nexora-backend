package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.AuthUserDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AuthDocumentRepository : ReactiveCrudRepository<AuthUserDocument, String> {
    fun existsByUserId(value: String): Mono<Boolean>
    fun findByEmail(value: String): Mono<AuthUserDocument>
}