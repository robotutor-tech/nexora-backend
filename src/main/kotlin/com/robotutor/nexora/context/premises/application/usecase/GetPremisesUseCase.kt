package com.robotutor.nexora.context.premises.application.usecase

import com.robotutor.nexora.context.premises.application.command.GetPremisesQuery
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class GetPremisesUseCase(
    private val premisesRepository: PremisesRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(query: GetPremisesQuery): Flux<PremisesAggregate> {
        return premisesRepository.findAllByPremisesIdIn(query.premisesIds)
            .logOnSuccess(logger = logger, message = "Successfully get premises")
            .logOnError(logger, "", "Failed to get premises")
    }
}