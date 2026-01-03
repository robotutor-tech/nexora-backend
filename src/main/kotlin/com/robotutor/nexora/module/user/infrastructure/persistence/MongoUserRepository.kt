package com.robotutor.nexora.module.user.infrastructure.persistence

import com.robotutor.nexora.common.cache.annotation.Cache
import com.robotutor.nexora.common.cache.annotation.CacheEvicts
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.domain.event.UserEventPublisher
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.module.user.infrastructure.persistence.mapper.UserDocumentMapper
import com.robotutor.nexora.module.user.infrastructure.persistence.repository.UserDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserRepository(
    private val userDocumentRepository: UserDocumentRepository,
    private val eventPublisher: UserEventPublisher,
) : UserRepository {
    @CacheEvicts(["user:user-aggregate:user-id:#userAggregate.userId", "user:user-aggregate:email:#email", "user:user-aggregate:email:#email:exists"])
    override fun save(userAggregate: UserAggregate): Mono<UserAggregate> {
        val userDocument = UserDocumentMapper.toMongoDocument(userAggregate)
        return userDocumentRepository.save(userDocument)
            .retryOptimisticLockingFailure()
            .map { UserDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, userAggregate)
    }

    @CacheEvicts(["user:user-aggregate:user-id:#userAggregate.userId", "user:user-aggregate:email:#email", "user:user-aggregate:email:#email:exists"])
    override fun deleteByUserId(userId: UserId): Mono<UserAggregate> {
        return userDocumentRepository.deleteByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:user-id:#userId")
    override fun findByUserId(userId: UserId): Mono<UserAggregate> {
        return userDocumentRepository.findByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:email:#email")
    override fun findByEmail(email: Email): Mono<UserAggregate> {
        return userDocumentRepository.findByEmail(email.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    @Cache("user:user-aggregate:email:#email:exists")
    override fun existsByEmail(email: Email): Mono<Boolean> {
        return userDocumentRepository.existsByEmail(email.value)
    }
}