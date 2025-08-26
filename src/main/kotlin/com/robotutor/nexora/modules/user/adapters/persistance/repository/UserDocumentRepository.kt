package com.robotutor.nexora.modules.user.adapters.persistence.repository

import com.robotutor.nexora.modules.user.adapters.persistence.model.UserDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserDocumentRepository : ReactiveMongoRepository<UserDocument, String> {
    fun findByEmail(value: String): Mono<UserDocument>
    fun deleteByUserId(userId: String): Mono<UserDocument>
    fun findByUserId(userId: String): Mono<UserDocument>
}
