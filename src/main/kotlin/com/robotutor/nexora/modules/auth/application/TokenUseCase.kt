package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.factory.TokenFactory
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.Tokens
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
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
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
        metadata: Map<String, String> = emptyMap()
    ): Mono<Token> {
        val strategy = tokenFactory.getStrategy(tokenType)
        val token = strategy.generate(principalType, principalContext, metadata)
        return tokenRepository.save(token)
            .logOnSuccess(logger, "Successfully generated token")
            .logOnError(logger, "", "Failed to generate token")
    }

    fun generateTokenWithRefreshToken(
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
    ): Mono<Tokens> {
        return generateToken(TokenType.AUTHORIZATION, principalType, principalContext)
            .flatMap { token ->
                val map = mapOf("authorizationToken" to token.tokenId.value)
                generateToken(TokenType.REFRESH, principalType, principalContext, map)
                    .map { Tokens(token, it) }
            }
    }

    fun invalidateToken(token: Token): Mono<Boolean> {
        return tokenRepository.invalidateToken(token)
            .logOnSuccess(logger, "Successfully invalidated token")
            .logOnError(logger, "", "Failed to invalidate token")
    }

    fun findTokenByValue(token: String): Mono<Token> {
        return tokenRepository.findByValue(token)
            .switchIfEmpty(
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
            )
    }

}