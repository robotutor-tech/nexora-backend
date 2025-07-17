package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.automation.services.ConditionService
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.createMonoEmpty
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AutomationValidator(
    private val conditionService: ConditionService,
    private val actionValidator: ActionValidator,
    private val triggerValidator: TriggerValidator
) {
    fun validateRequest(
        request: AutomationRequest,
        condition: ConditionNode?,
        premisesActorData: PremisesActorData
    ): Mono<AutomationRequest> {
        return validateConditionNode(condition, premisesActorData)
            .flatMap { triggerValidator.validateTriggers(request.triggers, premisesActorData) }
            .flatMap { actionValidator.validateActions(request.actions, premisesActorData) }
            .switchIfEmpty(
                triggerValidator.validateTriggers(request.triggers, premisesActorData)
                    .flatMap { actionValidator.validateActions(request.actions, premisesActorData) }
            )
            .map { request }
    }

    private fun validateConditionNode(
        condition: ConditionNode?,
        premisesActorData: PremisesActorData
    ): Mono<ConditionNode> {
        val errorCode = NexoraError.NEXORA0305.errorCode
        return if (condition == null) {
            createMonoEmpty()
        } else {
            when (condition) {
                is ConditionNot -> validateConditionNot(errorCode, condition, premisesActorData)
                is ConditionGroup -> validateConditionGroup(errorCode, condition, premisesActorData)
                is ConditionLeaf -> validateConditionLeaf(condition, premisesActorData)
            }
                .map { condition }
        }
    }

    private fun validateConditionGroup(
        errorCode: String,
        condition: ConditionGroup,
        premisesActorData: PremisesActorData
    ): Mono<ConditionGroup> {
        return if (condition.children.size < 2) {
            createMonoError(
                BadDataException(ErrorResponse(errorCode, "Condition group required at least 2 conditions"))
            )
        } else {
            createFlux(condition.children)
                .flatMap { validateConditionNode(it, premisesActorData) }
                .collectList()
                .map { condition }
        }
    }

    private fun validateConditionLeaf(
        condition: ConditionLeaf,
        premisesActorData: PremisesActorData
    ): Mono<ConditionLeaf> {
        return conditionService.getCondition(condition.conditionId, premisesActorData).map { condition }
    }

    private fun validateConditionNot(
        errorCode: String,
        condition: ConditionNot,
        premisesActorData: PremisesActorData
    ): Mono<ConditionNot> {
        return if (condition.child.type == ConditionNodeType.NOT) {
            createMonoError(BadDataException(ErrorResponse(errorCode, "NOT should not be child of NOT condition")))
        } else
            validateConditionNode(condition.child, premisesActorData)
                .map { condition }
    }
}