package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.event.IdentityEventPublisher
import com.robotutor.nexora.module.identity.domain.repository.SessionRepository
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.SessionDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.SessionDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoSessionRepository(
    private val sessionDocumentRepository: SessionDocumentRepository,
    private val eventPublisher: IdentityEventPublisher,
) : SessionRepository {
    override fun save(session: Session): Mono<Session> {
        val sessionDocument = SessionDocumentMapper.toMongoDocument(session)
        return sessionDocumentRepository.save(sessionDocument)
            .retryOptimisticLockingFailure()
            .map { SessionDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, session)
    }

    override fun findBySessionId(sessionId: SessionId): Mono<Session> {
        return sessionDocumentRepository.findBySessionIdAndExpiresAtAfter(sessionId.value)
            .map { SessionDocumentMapper.toDomainModel(it) }
    }
}
