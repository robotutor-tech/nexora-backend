package com.robotutor.nexora.context.user.infrastructure.persistence.repository

import com.robotutor.nexora.context.user.infrastructure.persistence.document.UserDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserDocumentRepository : ReactiveCrudRepository<UserDocument, String> {
    fun deleteByUserId(userId: String): Mono<UserDocument>
    fun findByUserId(userId: String): Mono<UserDocument>
    fun findByEmail(email: String): Mono<UserDocument>
}