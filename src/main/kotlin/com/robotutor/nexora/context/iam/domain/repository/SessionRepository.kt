package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

interface SessionRepository {
    fun save(sessionAggregate: SessionAggregate): Mono<SessionAggregate>
    fun findByTokenValueAndExpiredAtAfter(tokenValue: HashedTokenValue, expiresAt: Instant = Instant.now()): Mono<SessionAggregate>
}