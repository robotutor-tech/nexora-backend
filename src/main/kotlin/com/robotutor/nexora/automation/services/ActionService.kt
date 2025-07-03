package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.ActionId
import com.robotutor.nexora.automation.repositories.ActionRepository
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActionService(
    private val idGeneratorService: IdGeneratorService,
    private val actionRepository: ActionRepository
) {
    fun validateActions(actionIds: List<ActionId>, premisesActorData: PremisesActorData): Mono<List<ActionId>> {
        if (actionIds.isEmpty())
            return createMonoError(BadDataException(NexoraError.NEXORA0303))

        val uniqueIds = actionIds.toSet().toList()

        return actionRepository.findAllByActionIdInAndPremisesId(uniqueIds, premisesActorData.premisesId)
            .collectList()
            .flatMap { actions ->
                val missingIds = actions.map { it.actionId }.toSet() - uniqueIds
                if (missingIds.isEmpty()) {
                    createMonoError(
                        BadDataException(
                            ErrorResponse(
                                NexoraError.NEXORA0304.errorCode,
                                "Invalid action Ids: ${missingIds.joinToString(",")}"
                            )
                        )
                    )
                } else {
                    createMono(actionIds)
                }
            }
    }
}
