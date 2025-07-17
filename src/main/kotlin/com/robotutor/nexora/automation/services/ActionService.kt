package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.ActionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.Action
import com.robotutor.nexora.automation.models.ActionConfig
import com.robotutor.nexora.automation.models.ActionId
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.models.documents.ActionDocument
import com.robotutor.nexora.automation.repositories.ActionRepository
import com.robotutor.nexora.automation.services.converter.ActionConverter
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
import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ActionService(
    private val idGeneratorService: IdGeneratorService,
    private val actionRepository: ActionRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val actionValidator: ActionValidator,
    private val actionConverter: ActionConverter
) {

    val logger = Logger(this::class.java)

    fun createAction(request: ActionRequest, premisesActorData: PremisesActorData): Mono<Action> {
        return actionConverter.toActionConfig(request.config)
            .flatMap { actionValidator.validateRequest(it, premisesActorData) }
            .flatMap { config ->
                actionRepository.findByPremisesIdAndConfig(premisesActorData.premisesId, request.config)
                    .flatMap { action ->
                        val error = NexoraError.NEXORA0311
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with actionId: ${action.actionId}"
                        )
                        createMonoError<ActionConfig>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(config))
            }
            .flatMap { config ->
                idGeneratorService.generateId(IdType.ACTION_ID)
                    .map { actionId -> ActionDocument.from(actionId, config, request, premisesActorData) }
            }
            .flatMap {
                actionRepository.save(it)
                    .auditOnSuccess("AUTOMATION_ACTION_CREATED", mapOf("actionId" to it.actionId, "name" to it.name))
            }
            .flatMap { actionConverter.toAction(it) }
            .flatMap { action ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_ACTION, action.actionId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { action }
            }
            .logOnSuccess(logger, "Successfully created new Action")
            .logOnError(logger, "", "Failed to create new Action")
    }

    fun getActions(actionsIds: List<ActionId>, premisesActorData: PremisesActorData): Flux<Action> {
        return actionRepository.findAllByActionIdInAndPremisesId(actionsIds, premisesActorData.premisesId)
            .flatMap { actionConverter.toAction(it) }
    }

    fun getAction(actionsId: ActionId, premisesActorData: PremisesActorData): Mono<Action> {
        return actionRepository.findByActionIdAndPremisesId(actionsId, premisesActorData.premisesId)
            .flatMap { actionConverter.toAction(it) }
    }
}
