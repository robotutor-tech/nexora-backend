package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.CreateSessionCommand
import com.robotutor.nexora.module.identity.application.command.RefreshSessionCommand
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.repository.SessionRepository
import com.robotutor.nexora.module.identity.domain.service.TokenGenerator
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RefreshSessionService(
    private val tokenGenerator: TokenGenerator,
    private val sessionRepository: SessionRepository,
    private val createSessionService: CreateSessionService,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RefreshSessionCommand): Mono<Tokens> {
        val sessionData = tokenGenerator.getSession(command.token)
        return sessionRepository.findBySessionId(sessionData.sessionId)
            .required(UnAuthorizedException(IdentityError.NEXORA0205))
            .flatMap { session ->
                createSessionService.execute(CreateSessionCommand(session.accountData, SessionId.generate()))
            }
            .logOnSuccess(logger, "Successfully refreshed token")
            .logOnError(logger, "Failed to refresh token")
    }
}
