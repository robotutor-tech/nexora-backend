package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.models.Trigger
import com.robotutor.nexora.automation.models.TriggerConfig
import com.robotutor.nexora.automation.models.TriggerId
import com.robotutor.nexora.automation.models.documents.TriggerDocument
import com.robotutor.nexora.automation.repositories.TriggerRepository
import com.robotutor.nexora.automation.services.converter.TriggerConverter
import com.robotutor.nexora.automation.services.validator.TriggerValidator
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
import com.robotutor.nexora.webClient.exceptions.DataNotFoundException
import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TriggerService(
    private val idGeneratorService: IdGeneratorService,
    private val triggerRepository: TriggerRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val triggerValidator: TriggerValidator,
    private val triggerConverter: TriggerConverter
) {

    val logger = Logger(this::class.java)

    fun createTrigger(request: TriggerRequest, premisesActorData: PremisesActorData): Mono<Trigger> {
        return triggerConverter.toTriggerConfig(request.config)
            .flatMap { triggerValidator.validateConfig(it, premisesActorData) }
            .flatMap { config ->
                triggerRepository.findByPremisesIdAndConfig(premisesActorData.premisesId, request.config)
                    .flatMap { trigger ->
                        val error = NexoraError.NEXORA0310
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with triggerId: ${trigger.triggerId}"
                        )
                        createMonoError<TriggerConfig>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(config))
            }
            .flatMap { config ->
                idGeneratorService.generateId(IdType.TRIGGER_ID)
                    .map { triggerId -> TriggerDocument.from(triggerId, config, request, premisesActorData) }
            }
            .flatMap {
                triggerRepository.save(it)
                    .auditOnSuccess("AUTOMATION_TRIGGER_CREATED", mapOf("triggerId" to it.triggerId, "name" to it.name))
            }
            .flatMap { triggerConverter.toTrigger(it) }
            .flatMap { trigger ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_TRIGGER, trigger.triggerId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { trigger }
            }
            .logOnSuccess(logger, "Successfully created new Trigger")
            .logOnError(logger, "", "Failed to create new Trigger")
    }

    fun getAllTriggers(triggerIds: List<TriggerId>, premisesActorData: PremisesActorData): Flux<Trigger> {
        return triggerRepository.findAllByTriggerIdInAndPremisesId(triggerIds, premisesActorData.premisesId)
            .flatMap { triggerConverter.toTrigger(it) }
    }

    fun getTrigger(triggerId: TriggerId, premisesActorData: PremisesActorData): Mono<Trigger> {
        return triggerRepository.findByTriggerIdAndPremisesId(triggerId, premisesActorData.premisesId)
            .flatMap { triggerConverter.toTrigger(it) }
            .switchIfEmpty(
                createMonoError(DataNotFoundException(NexoraError.NEXORA0313))
            )
    }
}
