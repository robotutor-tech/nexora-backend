package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.models.Trigger
import com.robotutor.nexora.automation.models.TriggerId
import com.robotutor.nexora.automation.repositories.TriggerRepository
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
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TriggerService(
    private val idGeneratorService: IdGeneratorService,
    private val triggerRepository: TriggerRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val triggerValidator: TriggerValidator
) {

    val logger = Logger(this::class.java)

    fun validateTriggers(triggerIds: List<TriggerId>, premisesActorData: PremisesActorData): Mono<List<TriggerId>> {
        if (triggerIds.isEmpty())
            return createMonoError(BadDataException(NexoraError.NEXORA0301))

        val uniqueIds = triggerIds.toSet().toList()
        return getAllTriggers(uniqueIds, premisesActorData)
            .collectList()
            .flatMap { triggers ->
                val missingIds = triggers.map { it.triggerId }.toSet() - uniqueIds
                if (missingIds.isEmpty()) {
                    createMonoError(
                        BadDataException(
                            ErrorResponse(
                                NexoraError.NEXORA0302.errorCode,
                                "Invalid trigger Ids: ${missingIds.joinToString(",")}"
                            )
                        )
                    )
                } else {
                    createMono(triggerIds)
                }
            }
    }

    fun createTrigger(request: TriggerRequest, premisesActorData: PremisesActorData): Mono<Trigger> {
        return triggerValidator.validateRequest(request, premisesActorData)
            .flatMap { config ->
                idGeneratorService.generateId(IdType.TRIGGER_ID)
                    .map { triggerId -> Trigger.from(triggerId, config, request, premisesActorData) }
            }
            .flatMap {
                triggerRepository.save(it)
                    .auditOnSuccess("AUTOMATION_TRIGGER_CREATED", mapOf("triggerId" to it.triggerId, "name" to it.name))
            }
            .flatMap { trigger ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_TRIGGER, trigger.triggerId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { trigger }
            }
            .onErrorResume {
                if (it is DuplicateKeyException) {
                    // TODO: send the trigger id if user has the access.
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0310))
                } else {
                    createMonoError(it)
                }
            }
            .logOnSuccess(logger, "Successfully created new Trigger")
            .logOnError(logger, "", "Failed to create new Trigger")

    }

    fun getAllTriggers(triggerIds: List<TriggerId>, premisesActorData: PremisesActorData): Flux<Trigger> {
        return triggerRepository.findAllByTriggerIdInAndPremisesId(triggerIds, premisesActorData.premisesId)
    }
}
