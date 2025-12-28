package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val tokenGenerator: TokenGenerator,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

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