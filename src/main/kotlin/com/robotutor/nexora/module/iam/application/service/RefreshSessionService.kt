package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.module.iam.application.command.RefreshSessionCommand
import com.robotutor.nexora.module.iam.application.view.SessionTokens
import com.robotutor.nexora.module.iam.domain.exception.IAMError
import com.robotutor.nexora.module.iam.domain.repository.SessionRepository
import com.robotutor.nexora.module.iam.domain.service.SessionService
import com.robotutor.nexora.module.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RefreshSessionService(
    private val sessionRepository: SessionRepository,
    private val sessionService: SessionService,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RefreshSessionCommand): Mono<SessionTokens> {
        return sessionRepository.findByTokenValueAndExpiredAtAfter(command.token)
            .required(UnAuthorizedException(IAMError.NEXORA0205))
            .flatMap { session ->
                val refreshToken = TokenValue.generate()
                val refreshedSession = sessionService.refresh(session, refreshToken)
                sessionRepository.save(refreshedSession)
                    .map { SessionTokens(refreshedSession.getAccessToken(), refreshToken) }
            }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "Failed to refresh token")
    }
}