package com.robotutor.nexora.modules.user.adapters.persistance.repository

import com.robotutor.nexora.modules.user.adapters.persistance.model.UserDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserDocumentRepository : ReactiveMongoRepository<UserDocument, String> {
    fun existsByEmail(value: String): Mono<Boolean>
    fun deleteByUserId(userId: String): Mono<UserDocument>
    fun findByUserId(userId: String): Mono<UserDocument>
}
