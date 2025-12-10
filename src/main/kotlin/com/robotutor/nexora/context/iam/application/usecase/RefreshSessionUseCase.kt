package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.context.iam.application.command.RefreshSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

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
                val session = session.refresh(HashedTokenValue.create(refreshToken))
                val accessToken = tokenGenerator.generateAccessToken(TokenPayload(session.sessionPrincipal))
                sessionRepository.save(session).map { session }
                    .map { SessionTokens(accessToken, refreshToken) }
            }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "Failed to refresh token")
    }
}