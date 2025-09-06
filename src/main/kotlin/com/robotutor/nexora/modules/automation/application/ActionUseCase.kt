package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.automation.application.command.CreateActionCommand
import com.robotutor.nexora.modules.automation.application.validation.ConfigValidation
import com.robotutor.nexora.modules.automation.domain.entity.Action
import com.robotutor.nexora.modules.automation.domain.entity.ActionId
import com.robotutor.nexora.modules.automation.domain.entity.IdType
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.repository.ActionRepository
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
class ActionUseCase(
    private val configValidation: ConfigValidation,
    private val actionRepository: ActionRepository,
    private val idGeneratorService: IdGeneratorService,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>
) {

    private val logger = Logger(this::class.java)

    fun createAction(createActionCommand: CreateActionCommand, actorData: ActorData): Mono<Action> {
        return configValidation.validate(createActionCommand.config, actorData)
            .flatMap {
                actionRepository.findByPremisesIdAndConfig(actorData.premisesId, createActionCommand.config)
                    .flatMap { action ->
                        val error = NexoraError.NEXORA0311
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with actionId: ${action.actionId}"
                        )
                        createMonoError<ActionConfig>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(createActionCommand.config))
            }
            .flatMap {
                idGeneratorService.generateId(IdType.ACTION_ID, ActionId::class.java)
                    .map { actionId -> Action.create(actionId, createActionCommand, actorData) }
            }
            .flatMap { action ->
                val event = ResourceCreatedEvent(ResourceType.AUTOMATION_ACTION, ResourceId(action.actionId.value))
                actionRepository.save(action).map { action }
                    .publishEvent(resourceCreatedEventPublisher, event)
            }
            .logOnSuccess(logger, "Successfully created new Action")
            .logOnError(logger, "", "Failed to create new Action")
    }

    fun getActions(actionsIds: List<ActionId>, actorData: ActorData): Flux<Action> {
        return actionRepository.findAllByPremisesIdAndActionIdIn(actorData.premisesId, actionsIds)
    }

    fun getAction(actionId: ActionId, actorData: ActorData): Mono<Action> {
        return actionRepository.findByActionIdAndPremisesId(actionId, actorData.premisesId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0316)))
    }
}