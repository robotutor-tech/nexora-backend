package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.modules.auth.application.factory.TokenFactory
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.Tokens
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TokenUseCase(
    private val tokenFactory: TokenFactory,
    private val tokenRepository: TokenRepository,
) {
    private val logger = Logger(this::class.java)

    fun generateToken(
        tokenType: TokenType,
        identifier: Identifier<TokenIdentifier>,
        metadata: Map<String, Any?>
    ): Mono<Token> {
        val strategy = tokenFactory.getStrategy(tokenType)
        val token = strategy.generate(identifier, metadata)
        return tokenRepository.save(token)
            .logOnSuccess(logger, "Successfully generated token")
            .logOnError(logger, "", "Failed to generate token")
    }

    fun generateTokenWithRefreshToken(
        tokenType: TokenType,
        identifier: Identifier<TokenIdentifier>,
        metadata: Map<String, Any?>
    ): Mono<Tokens> {
        return generateToken(tokenType, identifier, metadata)
            .flatMap { token ->
                val mutableMap = metadata.toMutableMap()
                mutableMap["authorizationToken"] = token.tokenId.value
                generateToken(TokenType.REFRESH, identifier, mutableMap.toMap())
                    .map { Tokens(token, it) }
            }
    }

    fun invalidateToken(token: Token): Mono<Boolean> {
        return tokenRepository.invalidateToken(token)
            .logOnSuccess(logger, "Successfully invalidated token")
            .logOnError(logger, "", "Failed to invalidate token")
    }

}