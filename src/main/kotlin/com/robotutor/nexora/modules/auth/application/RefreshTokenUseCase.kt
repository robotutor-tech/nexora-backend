package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.RefreshTokenCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class RefreshTokenUseCase(
    private val tokenUseCase: TokenUseCase,
) {
    private val logger = Logger(this::class.java)

    fun refresh(refreshTokenCommand: RefreshTokenCommand): Mono<TokenResponses> {
        return tokenUseCase.findTokenByValue(refreshTokenCommand.token)
            .flatMap { token ->
                if (token.tokenType != TokenType.REFRESH) {
                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
                } else {
                    createMono(token)
                }
            }
            .flatMap { token ->
                tokenUseCase.generateTokenWithRefreshToken(token.principalType, token.principal)
                    .flatMap { tokens ->
                        tokenUseCase.invalidateToken(token).map { tokens }
                    }
            }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "", "Failed to refresh token")
    }
}