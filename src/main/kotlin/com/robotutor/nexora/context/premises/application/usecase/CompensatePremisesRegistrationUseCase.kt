package com.robotutor.nexora.context.premises.application.usecase

import com.robotutor.nexora.context.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensatePremisesRegistrationUseCase(
    private val premisesRepository: PremisesRepository,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    fun execute(command: CompensatePremisesRegistrationCommand): Mono<PremisesAggregate> {
        return premisesRepository.deleteByPremisesIdAndOwnerId(command.premisesId, command.ownerId)

            .flatMap { premises -> premisesRepository.save(premises) }
            .logOnSuccess(
                logger = logger,
                message = "Successfully activated premises",
                mapOf("premisesId" to command.premisesId)
            )
            .logOnError(logger, "Failed to activate premises", mapOf("premisesId" to command.premisesId))
    }
}