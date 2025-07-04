package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.ActionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.Action
import com.robotutor.nexora.automation.models.ActionId
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.repositories.ActionRepository
import com.robotutor.nexora.automation.services.validator.ActionValidator
import com.robotutor.nexora.iam.services.EntitlementResource
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActionService(
    private val idGeneratorService: IdGeneratorService,
    private val actionRepository: ActionRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val actionValidator: ActionValidator
) {

    val logger = Logger(this::class.java)

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

    fun createAction(request: ActionRequest, premisesActorData: PremisesActorData): Mono<Action> {
        return actionValidator.validateRequest(request, premisesActorData)
            .flatMap { idGeneratorService.generateId(IdType.ACTION_ID) }
            .map { actionId -> Action.from(actionId, request, premisesActorData) }
            .flatMap {
                actionRepository.save(it)
                    .auditOnSuccess(
                        "AUTOMATION_ACTION_CREATED",
                        mapOf("actionId" to it.actionId, "name" to it.name)
                    )
            }
            .flatMap { action ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_CONDITION, action.actionId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { action }
            }
            .logOnSuccess(logger, "Successfully created new Action")
            .logOnError(logger, "", "Failed to create new Action")
    }
}
