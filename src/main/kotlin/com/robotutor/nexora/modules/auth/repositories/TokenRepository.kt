package com.robotutor.nexora.modules.auth.repositories

import com.robotutor.nexora.modules.auth.models.Token
import com.robotutor.nexora.modules.auth.models.TokenId
import com.robotutor.nexora.common.security.models.TokenIdentifier
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.ZoneOffset

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, TokenId> {
    fun findByValueAndExpiresOnGreaterThan(token: String, now: Instant): Mono<Token>
    fun findByIdentifier_IdAndIdentifier_TypeAndExpiresOnGreaterThan(
        id: String,
        type: TokenIdentifier,
        expiresOn: Instant = Instant.now()
    ): Mono<Token>
}
