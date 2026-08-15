package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.AuthenticateActorCommand
import com.robotutor.nexora.module.identity.application.command.CreateSessionCommand
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.repository.ActorRepository
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateActorService(
    private val actorRepository: ActorRepository,
    private val createSessionService: CreateSessionService,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateActorCommand): Mono<Tokens> {
        return actorRepository.findByAccountIdAndPremisesId(command.accountData.accountId, command.premisesId)
            .required(BadDataException(IdentityError.NEXORA0207))
            .flatMap { actor ->
                val command = CreateSessionCommand(
                    accountData = ActorData(
                        actorId = actor.actorId,
                        premisesId = actor.premisesId,
                        accountId = actor.accountId,
                        subjectId = command.accountData.subjectId,
                        subjectType = command.accountData.subjectType
                    ),
                    sessionId = SessionId.generate()
                )
                createSessionService.execute(command)
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
