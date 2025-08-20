package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.model.Token
import reactor.core.publisher.Mono

interface TokenRepository {
    fun save(token: Token): Mono<Token>
    fun findByValue(value: String): Mono<Token>
    fun invalidateToken(token: Token): Mono<Boolean>
}