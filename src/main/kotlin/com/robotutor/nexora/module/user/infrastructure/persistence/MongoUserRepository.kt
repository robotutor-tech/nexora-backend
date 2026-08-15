package com.robotutor.nexora.module.user.infrastructure.persistence

import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.infrastructure.messaging.mapper.UserEventMapper
import com.robotutor.nexora.module.user.infrastructure.persistence.mapper.UserDocumentMapper
import com.robotutor.nexora.module.user.infrastructure.persistence.repository.UserDocumentRepository
import com.robotutor.nexora.shared.cache.annotation.Cache
import com.robotutor.nexora.shared.cache.annotation.CacheEvicts
import com.robotutor.nexora.shared.domain.vo.UserId
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserRepository(private val userDocumentRepository: UserDocumentRepository) : UserRepository {
    @CacheEvicts(["user:user-aggregate:user-id:#userAggregate.userId", "user:user-aggregate:email:#email", "user:user-aggregate:email:#email:exists"])
    override fun save(user: User): Mono<User> {
        val userDocument = UserDocumentMapper.toMongoDocument(user)
        return userDocumentRepository.save(userDocument)
            .retryOptimisticLockingFailure()
            .map { UserDocumentMapper.toDomainModel(it) }
            .publishEvents(user, UserEventMapper)
    }

    @CacheEvicts(["user:user-aggregate:user-id:#userAggregate.userId", "user:user-aggregate:email:#email", "user:user-aggregate:email:#email:exists"])
    override fun deleteByUserId(userId: UserId): Mono<User> {
        return userDocumentRepository.deleteByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:user-id:#userId")
    override fun findByUserId(userId: UserId): Mono<User> {
        return userDocumentRepository.findByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:email:#email")
    override fun findByEmail(email: Email): Mono<User> {
        return userDocumentRepository.findByEmail(email.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:email:#{email.value}:exists")
    override fun existsByEmail(email: Email): Mono<Boolean> {
        return userDocumentRepository.existsByEmail(email.value)
    }
}
