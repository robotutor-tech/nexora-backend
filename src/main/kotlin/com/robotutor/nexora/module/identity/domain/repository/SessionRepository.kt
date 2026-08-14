package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import reactor.core.publisher.Mono

interface SessionRepository {
    fun save(session: Session): Mono<Session>
    fun findBySessionId(sessionId: SessionId): Mono<Session>
}
