package com.robotutor.nexora.module.iam.infrastructure.persistence

import com.robotutor.nexora.module.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.module.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.iam.domain.repository.SessionRepository
import com.robotutor.nexora.module.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.module.iam.infrastructure.persistence.mapper.SessionDocumentMapper
import com.robotutor.nexora.module.iam.infrastructure.persistence.repository.SessionDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class MongoSessionRepository(
    private val sessionDocumentRepository: SessionDocumentRepository,
    private val eventPublisher: IAMEventPublisher,
) : SessionRepository {
    override fun save(sessionAggregate: SessionAggregate): Mono<SessionAggregate> {
        val sessionDocument = SessionDocumentMapper.toMongoDocument(sessionAggregate)
        return sessionDocumentRepository.save(sessionDocument)
            .retryOptimisticLockingFailure()
            .map { SessionDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, sessionAggregate)
    }

    override fun findByTokenValueAndExpiredAtAfter(
        tokenValue: HashedTokenValue,
        expiresAt: Instant
    ): Mono<SessionAggregate> {
        return sessionDocumentRepository.findByRefreshTokenHashAndExpiresAtAfter(tokenValue.hashedValue, expiresAt)
            .map { SessionDocumentMapper.toDomainModel(it) }
    }
}