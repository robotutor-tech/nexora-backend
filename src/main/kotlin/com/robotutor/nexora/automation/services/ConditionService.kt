package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.ConditionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.Condition
import com.robotutor.nexora.automation.models.ConditionConfig
import com.robotutor.nexora.automation.models.ConditionId
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.models.documents.ConditionDocument
import com.robotutor.nexora.automation.repositories.ConditionRepository
import com.robotutor.nexora.automation.services.converter.ConditionConverter
import com.robotutor.nexora.automation.services.validator.ConditionValidator
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
import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ConditionService(
    private val idGeneratorService: IdGeneratorService,
    private val conditionRepository: ConditionRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val conditionValidator: ConditionValidator,
    private val conditionConverter: ConditionConverter
) {

    val logger = Logger(this::class.java)

    fun getConditionByConditionId(conditionId: ConditionId, premisesActorData: PremisesActorData): Mono<Condition> {
        return conditionRepository.findByConditionIdAndPremisesId(conditionId, premisesActorData.premisesId)
            .flatMap { conditionConverter.toCondition(it) }
            .switchIfEmpty(
                createMonoError(BadDataException(NexoraError.NEXORA0306))
            )
    }

    fun createCondition(request: ConditionRequest, premisesActorData: PremisesActorData): Mono<Condition> {
        return conditionConverter.toConditionConfig(request.config)
            .flatMap { conditionValidator.validateRequest(it) }
            .flatMap { config ->
                conditionRepository.findByPremisesIdAndConfig(premisesActorData.premisesId, request.config)
                    .flatMap { condition ->
                        val error = NexoraError.NEXORA0312
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with conditionId: ${condition.conditionId}",
                        )
                        createMonoError<ConditionConfig>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(config))
            }
            .flatMap { config ->
                idGeneratorService.generateId(IdType.CONDITION_ID)
                    .map { conditionId -> ConditionDocument.from(conditionId, config, request, premisesActorData) }
            }
            .flatMap {
                conditionRepository.save(it)
                    .auditOnSuccess(
                        "AUTOMATION_CONDITION_CREATED",
                        mapOf("conditionId" to it.conditionId, "name" to it.name)
                    )
            }
            .flatMap { conditionConverter.toCondition(it) }
            .flatMap { condition ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_CONDITION, condition.conditionId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { condition }
            }
            .logOnSuccess(logger, "Successfully created new Condition")
            .logOnError(logger, "", "Failed to create new Condition")
    }

    fun getConditions(conditionIds: List<ConditionId>, premisesActorData: PremisesActorData): Flux<Condition> {
        return conditionRepository.findAllByConditionIdInAndPremisesId(conditionIds, premisesActorData.premisesId)
            .flatMap { conditionConverter.toCondition(it) }
    }

    fun getCondition(conditionId: ConditionId, premisesActorData: PremisesActorData): Mono<Condition> {
        return conditionRepository.findByConditionIdAndPremisesId(conditionId, premisesActorData.premisesId)
            .flatMap { conditionConverter.toCondition(it) }
            .switchIfEmpty(createMonoError(BadDataException(NexoraError.NEXORA0315)))
    }
}
