package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RefreshSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.exception.NexoraError
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RefreshSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val tokenGenerator: TokenGenerator
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RefreshSessionCommand): Mono<SessionTokens> {
        val hashedTokenValue = HashedTokenValue.create(command.token)
        return sessionRepository.findByTokenValueAndExpiredAtAfter(hashedTokenValue)
            .switchIfEmpty(createMonoError(UnAuthorizedException(NexoraError.NEXORA0205)))
            .flatMap { session ->
                val refreshToken = TokenValue.generate()
                val refreshedSession = session.refresh(HashedTokenValue.create(refreshToken))
                val accessToken = tokenGenerator.generateAccessToken(TokenPayload(refreshedSession.sessionPrincipal))
                sessionRepository.save(refreshedSession).map { refreshedSession }
                    .map { SessionTokens(accessToken, refreshToken) }
            }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "Failed to refresh token")
    }
}