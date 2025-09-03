package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.entity.Token
import com.robotutor.nexora.modules.auth.domain.entity.TokenId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

interface TokenRepository {
    fun save(token: Token): Mono<Token>
    fun findByTokenId(tokenId: TokenId): Mono<Token>
    fun findAllByTokenIdIn(tokenIds: List<TokenId>): Flux<Token>
    fun findByValueAndExpiredAtAfter(tokenValue: String, expiresAt: Instant): Mono<Token>
}