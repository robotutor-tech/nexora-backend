package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.CreateSessionCommand
import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.repository.SessionRepository
import com.robotutor.nexora.module.identity.domain.service.TokenGenerator
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.service.SessionExpiryService
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class CreateSessionService(
    private val sessionRepository: SessionRepository,
    private val tokenGenerator: TokenGenerator,
    private val secretEncoder: SecretEncoder,
    private val sessionExpiryService: SessionExpiryService
) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: CreateSessionCommand): Mono<Tokens> {
        val tokens = tokenGenerator.generateTokens(command.principalData, command.sessionId)
        val token = secretEncoder.encode(tokens.refreshToken)
        val expiresAt = sessionExpiryService.getExpiryForRefreshToken(command.principalData)
        val session = Session.register(command.sessionId, command.principalData, token, expiresAt)
        return sessionRepository.save(session)
            .auditOnSuccess(
                "SESSION_CREATED",
                ResourceType.SESSION,
                session.sessionId,
                principal = command.principalData
            )
            .logOnSuccess(logger, "Successfully created new session")
            .logOnError(logger, "Failed to create new session")
            .map { tokens }
    }
}
