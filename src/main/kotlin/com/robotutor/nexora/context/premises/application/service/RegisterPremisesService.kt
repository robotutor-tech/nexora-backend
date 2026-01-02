package com.robotutor.nexora.context.premises.application.service

import com.robotutor.nexora.context.premises.application.command.RegisterPremisesCommand
import com.robotutor.nexora.context.premises.domain.policy.RegisterPremisesPolicy
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.exceptions.PremisesError
import com.robotutor.nexora.context.premises.domain.repository.PremisesIdGenerator
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.evaluatePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesService(
    private val registerPremisesPolicy: RegisterPremisesPolicy,
    private val premisesIdGenerator: PremisesIdGenerator,
    private val premisesRepository: PremisesRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterPremisesCommand): Mono<PremisesAggregate> {
        return evaluatePolicy(registerPremisesPolicy, command.owner, PremisesError.NEXORA0501)
            .flatMap { premisesIdGenerator.generate() }
            .map { premisesId ->
                PremisesAggregate.register(
                    premisesId = premisesId,
                    name = command.name,
                    ownerId = command.owner.accountId,
                    address = command.address
                )
            }
            .flatMap { premises -> premisesRepository.save(premises) }
            .logOnSuccess(logger = logger, message = "Successfully registered premises")
            .logOnError(logger, "Failed to register premises")
    }
}