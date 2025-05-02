package com.robotutor.nexora.premises.services

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.controllers.view.PremisesCreateRequest
import com.robotutor.nexora.premises.models.IdType
import com.robotutor.nexora.premises.models.Premises
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.premises.repositories.PremisesRepository
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PremisesService(
    private val premisesRepository: PremisesRepository,
    private val idGeneratorService: IdGeneratorService
) {
    val logger = Logger(this::class.java)

    fun createPremises(premisesRequest: PremisesCreateRequest, authUserData: AuthUserData): Mono<Premises> {
        return idGeneratorService.generateId(IdType.PREMISE_ID)
            .map { premisesId -> Premises.from(premisesId, premisesRequest.name, authUserData.userId) }
            .flatMap { premises -> premisesRepository.save(premises) }
            .logOnSuccess(logger, "Successfully created premise")
            .logOnError(logger, "", "Failed to create premise")
    }

    fun getPremises(premisesIds: List<PremisesId>): Flux<Premises> {
        return premisesRepository.findAllByPremisesIdIn(premisesIds)
    }

    fun getPremisesDetails(premisesActorData: PremisesActorData): Mono<Premises> {
        return premisesRepository.findByPremisesId(premisesActorData.premisesId)
    }
}
