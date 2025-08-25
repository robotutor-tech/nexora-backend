package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.application.factory.TokenFactory
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.Tokens
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
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
        val token = tokenFactory.getStrategy(tokenType)
            .generate(principalType, principalContext, metadata)
        return tokenRepository.save(token)
            .logOnSuccess(logger, "Successfully generated token")
            .logOnError(logger, "", "Failed to generate token")
    }

    fun generateTokenWithRefreshToken(
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
    ): Mono<TokenResponses> {
        return generateToken(TokenType.AUTHORIZATION, principalType, principalContext)
            .flatMap { token ->
                val map = mapOf("authorizationToken" to token.tokenId.value)
                generateToken(TokenType.REFRESH, principalType, principalContext, map)
                    .map { Tokens(token, it) }
            }
            .map { TokenResponses.from(it) }
    }

    fun invalidateToken(token: Token): Mono<Token> {
        return tokenRepository.save(token.invalidate())
            .publishEvents()
            .flatMap { invalidatedToken ->
                val tokenId = TokenId((invalidatedToken.metadata["authorizationToken"] ?: "") as String)
                tokenRepository.findByTokenId(tokenId)
                    .flatMap { tokenRepository.save(it.invalidate()) }
                    .publishEvents()
                    .switchIfEmpty(createMono(invalidatedToken))
            }
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