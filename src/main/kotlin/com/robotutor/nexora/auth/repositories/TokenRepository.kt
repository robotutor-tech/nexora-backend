package com.robotutor.nexora.auth.repositories

import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.models.TokenId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, TokenId> {
    fun findByValue(token: String): Mono<Token>
    fun findByTokenId(tokenId: TokenId): Mono<Token>

}
