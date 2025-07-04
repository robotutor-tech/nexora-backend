package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.ConditionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.Condition
import com.robotutor.nexora.automation.models.ConditionGroup
import com.robotutor.nexora.automation.models.ConditionId
import com.robotutor.nexora.automation.models.ConditionLeaf
import com.robotutor.nexora.automation.models.ConditionNode
import com.robotutor.nexora.automation.models.ConditionNot
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.repositories.ConditionRepository
import com.robotutor.nexora.iam.services.EntitlementResource
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.BadDataException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConditionService(
    private val idGeneratorService: IdGeneratorService,
    private val conditionRepository: ConditionRepository,
    private val kafkaPublisher: KafkaPublisher
) {

    val logger = Logger(this::class.java)

    fun validateConditionNode(condition: ConditionNode?, premisesActorData: PremisesActorData): Mono<Boolean> {
        if (condition == null) {
            return createMono(true)
        }

        return when (condition) {
            is ConditionGroup -> {
                if (condition.children.isEmpty()) {
                    createMonoError(BadDataException(NexoraError.NEXORA0305))
                } else {
                    createFlux(condition.children)
                        .flatMap { validateConditionNode(it, premisesActorData) }
                        .then(createMono(true))
                }
            }

            is ConditionNot -> {
                validateConditionNode(condition.child, premisesActorData)
            }

            is ConditionLeaf -> {
                getConditionByConditionId(condition.conditionId, premisesActorData).map { true }
            }
        }

    }

    fun getConditionByConditionId(conditionId: ConditionId, premisesActorData: PremisesActorData): Mono<Condition> {
        return conditionRepository.findByConditionIdAndPremisesId(conditionId, premisesActorData.premisesId)
            .switchIfEmpty(
                createMonoError(BadDataException(NexoraError.NEXORA0306))
            )
    }

    fun createCondition(request: ConditionRequest, premisesActorData: PremisesActorData): Mono<Condition> {
        return validateConditionRequest(request, premisesActorData)
            .flatMap { idGeneratorService.generateId(IdType.CONDITION_ID) }
            .map { conditionId -> Condition.from(conditionId, request, premisesActorData) }
            .flatMap {
                conditionRepository.save(it)
                    .auditOnSuccess(
                        "AUTOMATION_CONDITION_CREATED",
                        mapOf("conditionId" to it.conditionId, "name" to it.name)
                    )
            }
            .flatMap { condition ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_CONDITION, condition.conditionId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { condition }
            }
            .logOnSuccess(logger, "Successfully created new Condition")
            .logOnError(logger, "", "Failed to create new Condition")
    }

    private fun validateConditionRequest(
        request: ConditionRequest,
        premisesActorData: PremisesActorData
    ): Mono<Boolean> {
        return createMono(true)
    }

}
