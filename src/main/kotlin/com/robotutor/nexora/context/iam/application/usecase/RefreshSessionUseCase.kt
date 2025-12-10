package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class RefreshSessionUseCase(private val sessionRepository: SessionRepository) {
    private val logger = Logger(this::class.java)
//
//    fun execute(command: RefreshTokensCommand): Mono<TokenResponses> {
//        return sessionRepository.findByTokenValueAndExpiredAtAfter(command.token)
//            .flatMap { token ->
//                if (token.tokenType != TokenType.REFRESH) {
//                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
//                } else {
//                    createMono(token)
//                }
//            }
//            .flatMap { token ->
//                tokenUseCase.generateTokenWithRefreshToken(token.principalType, token.principal)
//                    .flatMap { tokens ->
//                        tokenUseCase.invalidateToken(token).map { tokens }
//                    }
//            }
//            .logOnSuccess(logger, "Successfully refreshed token")
//            .logOnError(logger, "", "Failed to refresh token")
//    }
}