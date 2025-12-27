package com.robotutor.nexora.context.user.infrastructure.persistence

import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.event.UserEventPublisher
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.infrastructure.persistence.mapper.UserDocumentMapper
import com.robotutor.nexora.context.user.infrastructure.persistence.repository.UserDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserRepository(
    private val userDocumentRepository: UserDocumentRepository,
    private val eventPublisher: UserEventPublisher,
) : UserRepository {
    override fun save(userAggregate: UserAggregate): Mono<UserAggregate> {
        val userDocument = UserDocumentMapper.toMongoDocument(userAggregate)
        return userDocumentRepository.save(userDocument)
            .retryOptimisticLockingFailure()
            .map { UserDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, userAggregate)
    }

    override fun deleteByUserId(userId: UserId): Mono<UserAggregate> {
        return userDocumentRepository.deleteByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    override fun findByUserId(userId: UserId): Mono<UserAggregate> {
        return userDocumentRepository.findByUserId(userId.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }

    override fun findByEmail(email: Email): Mono<UserAggregate> {
        return userDocumentRepository.findByEmail(email.value)
            .map { UserDocumentMapper.toDomainModel(it) }
    }
}