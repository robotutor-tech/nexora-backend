package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.automation.application.command.CreateRuleCommand
import com.robotutor.nexora.modules.automation.application.validation.ConfigValidation
import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.domain.entity.IdType
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.repository.RuleRepository
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
class RuleUseCase(
    private val ruleRepository: RuleRepository,
    private val idGeneratorService: IdGeneratorService,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>,
    private val configValidation: ConfigValidation
) {

    private val logger = Logger(this::class.java)

    fun createRule(createRuleCommand: CreateRuleCommand, actorData: ActorData): Mono<Rule> {
        return configValidation.validate(createRuleCommand.config, actorData)
            .flatMap {
                ruleRepository.findByTypeAndPremisesIdAndConfig(
                    createRuleCommand.type,
                    actorData.premisesId,
                    createRuleCommand.config
                )
                    .flatMap { rule ->
                        val error = NexoraError.NEXORA0302
                        val errorResponse = ErrorResponse(
                            error.errorCode,
                            error.message + " with ruleId: ${rule.ruleId.value}"
                        )
                        createMonoError<Config>(DuplicateDataException(errorResponse))
                    }
                    .switchIfEmpty(createMono(createRuleCommand.config))
            }
            .flatMap {
                idGeneratorService.generateId(IdType.RULE_ID, RuleId::class.java)
                    .map { ruleId -> Rule.create(ruleId, createRuleCommand, actorData) }
            }
            .flatMap { rule ->
                val event = ResourceCreatedEvent(ResourceType.AUTOMATION_RULE, ResourceId(rule.ruleId.value))
                ruleRepository.save(rule).map { rule }
                    .publishEvent(resourceCreatedEventPublisher, event)
            }
            .logOnSuccess(logger, "Successfully created new Rule")
            .logOnError(logger, "", "Failed to create new Rule")
    }

    fun getRules(triggersIds: List<RuleId>, actorData: ActorData): Flux<Rule> {
        return ruleRepository.findAllByPremisesIdAndRuleIdIn(actorData.premisesId, triggersIds)
    }

    fun getRule(ruleId: RuleId, actorData: ActorData): Mono<Rule> {
        return ruleRepository.findByRuleIdAndPremisesId(ruleId, actorData.premisesId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0316)))
    }
}