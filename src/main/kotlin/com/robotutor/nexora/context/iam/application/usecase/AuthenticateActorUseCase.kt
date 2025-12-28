package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.AuthenticateActorCommand
import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.event.ActorAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.domain.vo.ActorPrincipal
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateActorUseCase(
    private val createSessionUseCase: CreateSessionUseCase,
    private val actorRepository: ActorRepository,
    private val eventPublisher: EventPublisher<IAMEvent>,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateActorCommand): Mono<SessionTokens> {
        return actorRepository.findByAccountIdAndPremisesId(command.accountData.accountId, command.premisesId)
            .flatMap { actor ->
                val createSessionCommand = CreateSessionCommand(
                    ActorPrincipal(
                        actor.actorId,
                        actor.premisesId,
                        actor.accountId,
                        command.accountData.type,
                        command.accountData.principalId
                    )
                )
                createSessionUseCase.execute(createSessionCommand)
                    .publishEvent(
                        eventPublisher, ActorAuthenticatedEvent(
                            actorId = actor.actorId,
                            premisesId = actor.premisesId,
                            accountId = actor.accountId,
                            type = command.accountData.type
                        )
                    )
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