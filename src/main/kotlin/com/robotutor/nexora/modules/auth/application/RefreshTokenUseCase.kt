package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.RefreshTokenCommand
import com.robotutor.nexora.modules.auth.application.command.ValidateTokenCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.application.dto.TokenValidationResult
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class RefreshTokenUseCase(
    private val tokenRepository: TokenRepository,
    private val tokenUseCase: TokenUseCase,
) {
    private val logger = Logger(this::class.java)

    fun refresh(refreshTokenCommand: RefreshTokenCommand): Mono<TokenResponses> {
        return tokenRepository.findByValue(refreshTokenCommand.token)
            .switchIfEmpty(
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
            )
            .flatMap { token ->
                if (token.tokenType != TokenType.REFRESH || token.expiresAt.isBefore(Instant.now())) {
                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
                } else {
                    tokenUseCase.generateTokenWithRefreshToken(
                        TokenType.AUTHORIZATION,
                        token.identifier,
                        token.metadata
                    )
                        .flatMap { tokens ->
                            tokenUseCase.invalidateToken(token)
                                .map { tokens }
                        }
                }
            }
            .map { TokenResponses.from(it) }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "", "Failed to refresh token")
    }
}