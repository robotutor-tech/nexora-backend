package com.robotutor.nexora.module.iam.domain.repository

import com.robotutor.nexora.module.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.module.iam.domain.vo.HashedTokenValue
import reactor.core.publisher.Mono
import java.time.Instant

interface SessionRepository {
    fun save(sessionAggregate: SessionAggregate): Mono<SessionAggregate>
    fun findByTokenValueAndExpiredAtAfter(tokenValue: HashedTokenValue, expiresAt: Instant = Instant.now()): Mono<SessionAggregate>
}