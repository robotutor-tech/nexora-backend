package com.robotutor.nexora.modules.user.adapters.persistance.repository

import com.robotutor.nexora.modules.user.adapters.persistance.model.UserDocument
import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoUserDocumentRepository(private val userDocumentRepository: UserDocumentRepository) : UserRepository {
    override fun save(user: User): Mono<User> {
        return userDocumentRepository.save(UserDocument.from(user))
            .map { it.toDomainModel() }
    }

    override fun existsByEmail(email: Email): Mono<Boolean> {
        return userDocumentRepository.existsByEmail(email.value)
    }

    override fun deleteByUserId(userId: UserId): Mono<User> {
        return userDocumentRepository.deleteByUserId(userId.value)
            .map { it.toDomainModel() }
    }
}