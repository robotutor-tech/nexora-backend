package com.robotutor.nexora.context.iam.application.service

import com.robotutor.nexora.context.iam.application.command.AuthenticateActorCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.event.ActorAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.service.SessionService
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateActorService(
    private val sessionService: SessionService,
    private val actorRepository: ActorRepository,
    private val eventPublisher: IAMEventPublisher,
    private val sessionRepository: SessionRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateActorCommand): Mono<SessionTokens> {
        return actorRepository.findByAccountIdAndPremisesId(command.accountData.accountId, command.premisesId)
            .flatMap { actor ->
                val refreshToken = TokenValue.generate(240)
                val session = sessionService.create(actor, command.accountData, refreshToken)
                val event = ActorAuthenticatedEvent(
                    actorId = actor.actorId,
                    premisesId = actor.premisesId,
                    accountId = actor.accountId,
                    type = command.accountData.type
                )
                sessionRepository.save(session)
                    .publishEvent(
                        eventPublisher, event
                    )
                    .map { SessionTokens(session.getAccessToken(), refreshToken) }
            }
            .logOnSuccess(
                logger,
                "Successfully authenticated actor",
                mapOf("account" to command.accountData, "premisesId" to command.premisesId)
            )
            .logOnError(
                logger,
                "Failed to authenticate actor",
                mapOf("account" to command.accountData, "premisesId" to command.premisesId)
            )
    }
}