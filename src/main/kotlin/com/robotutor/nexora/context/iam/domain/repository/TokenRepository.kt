package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.vo.TokenId
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

interface TokenRepository {
    fun save(tokenAggregate: TokenAggregate): Mono<TokenAggregate>
    fun findByTokenId(tokenId: TokenId): Mono<TokenAggregate>
    fun findAllByTokenIdIn(tokenIds: List<TokenId>): Flux<TokenAggregate>
    fun findByValueAndExpiredAtAfter(tokenValue: TokenValue, expiresAt: Instant): Mono<TokenAggregate>
}