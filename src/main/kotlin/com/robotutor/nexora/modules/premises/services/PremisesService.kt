package com.robotutor.nexora.modules.premises.services

import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.PremisesId
import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PremisesService(
) {
    val logger = Logger(this::class.java)

//    fun createPremises(premisesRequest: PremisesCreateRequest, authUserData: AuthUserData): Mono<Premises> {
//        return idGeneratorService.generateId(IdType.PREMISE_ID)
//            .map { premisesId -> Premises.from(premisesId, premisesRequest.name, authUserData.userId) }
//            .flatMap { premises ->
//                premisesRepository.save(premises)
//                    .auditOnSuccess(
//                        "PREMISES_CREATED",
//                        mapOf("premisesId" to premises.premisesId, "name" to premises.name),
//                        premisesId = premises.premisesId
//                    )
//            }
//            .logOnSuccess(logger, "Successfully created premise")
//            .logOnError(logger, "", "Failed to create premise")
//    }

//    fun getPremises(premisesIds: List<PremisesId>): Flux<Premises> {
//        return premisesRepository.findAllByPremisesIdIn(premisesIds)
//    }
//
//    fun getPremisesDetails(premisesActorData: PremisesActorData): Mono<Premises> {
//        return premisesRepository.findByPremisesId(premisesActorData.premisesId)
//    }
//
//    fun deletePremises(premisesId: PremisesId): Mono<Premises> {
//        return premisesRepository.deleteByPremisesId(premisesId)
//            .auditOnSuccess("PREMISES_DELETED", mapOf("premisesId" to premisesId), premisesId = premisesId)
//            .logOnSuccess(logger, "Successfully deleted premise")
//            .logOnError(logger, "", "Failed to delete premise")
//    }
}
