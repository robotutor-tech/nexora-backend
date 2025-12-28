package com.robotutor.nexora.context.premises.application.usecase

import com.robotutor.nexora.context.premises.application.command.CompensatePremisesRegistrationCommand
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensatePremisesRegistrationUseCase(
    private val premisesRepository: PremisesRepository,
    
) {
    private val logger = Logger(this::class.java)

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