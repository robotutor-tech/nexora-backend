package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.AuthUserDocument
import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.repository.AuthRepository
import com.robotutor.nexora.shared.domain.model.Email
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class MongoAuthRepository(private val authDocumentRepository: AuthDocumentRepository) : AuthRepository {
    override fun save(authUser: AuthUser): Mono<AuthUser> {
        return authDocumentRepository.save(AuthUserDocument.from(authUser))
            .map { it.toDomainModel() }
    }

    override fun findByEmail(email: Email): Mono<AuthUser> {
        return authDocumentRepository.findByEmail(email.value)
            .map { it.toDomainModel() }
    }
}