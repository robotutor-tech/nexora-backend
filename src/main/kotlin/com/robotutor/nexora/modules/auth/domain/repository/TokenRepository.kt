package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

interface TokenRepository {
    fun save(token: Token): Mono<Token>
    fun findByTokenId(tokenId: TokenId): Mono<Token>
    fun findAllByTokenIdIn(tokenIds: List<TokenId>): Flux<Token>
    fun findByValueAndExpiredAtAfter(tokenValue: String, expiresAt: Instant): Mono<Token>
}