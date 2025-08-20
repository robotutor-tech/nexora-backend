package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.auth.adapters.persistance.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class MongoTokenRepository(
    private val tokenRepository: TokenDocumentRepository,
) : TokenRepository {

    override fun save(token: Token): Mono<Token> {
        return tokenRepository.save(TokenDocument.from(token))
            .map { it.toDomainModel() }
    }

    override fun findByValue(value: String): Mono<Token> {
        return tokenRepository.findByValue(value)
            .map { it.toDomainModel() }
    }

    override fun invalidateToken(token: Token): Mono<Boolean> {
        return tokenRepository.deleteByTokenId(token.tokenId.value)
            .map { tokenDocument -> (tokenDocument.metadata["authorizationToken"] ?: "") as String }
            .flatMap { tokenId -> tokenRepository.deleteByTokenId(tokenId) }
            .map { true }
            .onErrorResume { createMono(true) }
    }
}