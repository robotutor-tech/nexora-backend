package com.robotutor.nexora.module.premises.application.service

import com.robotutor.nexora.module.premises.application.command.GetAllPremisesQuery
import com.robotutor.nexora.module.premises.application.command.GetPremisesQuery
import com.robotutor.nexora.module.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.module.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetPremisesService(
    private val premisesRepository: PremisesRepository,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(query: GetAllPremisesQuery): Flux<PremisesAggregate> {
        return premisesRepository.findAllByPremisesIdIn(query.premisesIds)
            .logOnSuccess(logger = logger, message = "Successfully get premises")
            .logOnError(logger, "Failed to get premises")
    }

    fun execute(query: GetPremisesQuery): Mono<PremisesAggregate> {
        return premisesRepository.findByPremisesId(query.premisesId)
            .logOnSuccess(logger = logger, message = "Successfully get premises")
            .logOnError(logger, "Failed to get premises ")
    }
}