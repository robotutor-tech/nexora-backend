package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.automation.application.command.CreateTriggerCommand
import com.robotutor.nexora.modules.automation.application.validation.ConfigValidation
import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.TriggerId
import com.robotutor.nexora.modules.automation.domain.entity.IdType
import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.modules.automation.domain.repository.TriggerRepository
import com.robotutor.nexora.modules.automation.domain.exception.NexoraError
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.DuplicateDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TriggerUseCase(
    private val triggerRepository: TriggerRepository,
    private val idGeneratorService: IdGeneratorService,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>,
    private val configValidation: ConfigValidation
) {

    private val logger = Logger(this::class.java)

    fun createTrigger(createTriggerCommand: CreateTriggerCommand, actorData: ActorData): Mono<Trigger> {
        return configValidation.validate(createTriggerCommand.config, actorData)
            .flatMap {
                triggerRepository.findByPremisesIdAndConfig(actorData.premisesId, createTriggerCommand.config)
                    .flatMap { trigger ->
                        val error = NexoraError.NEXORA0311
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with triggerId: ${trigger.triggerId}"
                        )
                        createMonoError<TriggerConfig>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(createTriggerCommand.config))
            }
            .flatMap {
                idGeneratorService.generateId(IdType.TRIGGER_ID, TriggerId::class.java)
                    .map { triggerId -> Trigger.create(triggerId, createTriggerCommand, actorData) }
            }
            .flatMap { trigger ->
                val event = ResourceCreatedEvent(ResourceType.AUTOMATION_ACTION, ResourceId(trigger.triggerId.value))
                triggerRepository.save(trigger).map { trigger }
                    .publishEvent(resourceCreatedEventPublisher, event)
            }
            .logOnSuccess(logger, "Successfully created new Trigger")
            .logOnError(logger, "", "Failed to create new Trigger")
    }

    fun getTriggers(triggersIds: List<TriggerId>, actorData: ActorData): Flux<Trigger> {
        return triggerRepository.findAllByPremisesIdAndTriggerIdIn(actorData.premisesId, triggersIds)
    }

    fun getTrigger(triggerId: TriggerId, actorData: ActorData): Mono<Trigger> {
        return triggerRepository.findByTriggerIdAndPremisesId(triggerId, actorData.premisesId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0316)))
    }
}