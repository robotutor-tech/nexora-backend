package com.robotutor.nexora.modules.premises.services

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

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
