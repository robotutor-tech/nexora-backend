package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class CreateSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val tokenGenerator: TokenGenerator
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CreateSessionCommand): Mono<SessionTokens> {
        val tokenPayload = TokenPayload(
            sessionPrincipal = command.sessionPrincipal,
        )
        val accessToken = tokenGenerator.generateAccessToken(tokenPayload)
        val refreshToken = TokenValue.generate()
        val session = SessionAggregate.create(
            sessionPrincipal = command.sessionPrincipal,
            refreshTokenHash = HashedTokenValue.create(refreshToken)
        )

        return sessionRepository.save(session)
            .map { session }
            .logOnSuccess(
                logger,
                "Successfully generated session",
                mapOf("sessionPrincipal" to command.sessionPrincipal)
            )
            .logOnError(logger, "Failed to generate session for", mapOf("sessionPrincipal" to command.sessionPrincipal))
            .map { SessionTokens(accessToken, refreshToken) }
    }
}