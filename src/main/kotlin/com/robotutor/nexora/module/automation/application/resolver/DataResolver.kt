package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.module.automation.domain.vo.component.data.ConditionSpecificationData
import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DataResolver(
    private val resolverStrategyFactory: ComponentResolverStrategyFactory,
) {

    fun resolve(command: CreateAutomationCommand): Mono<ResolvedAutomation> {
        return Mono.zip(
            resolveList(command.triggers.values),
//            resolveCondition(command.condition),
            resolveList(command.actions.values)
        )
            .map { tuple ->
                ResolvedAutomation(
                    automationId = AutomationId.generate(),
                    premisesId = command.premisesId,
                    name = command.name,
                    description = command.description,
                    triggers = tuple.t1,
                    condition = null,
                    actions = tuple.t2,
                    executionMode = command.executionMode,
                )
            }
    }

    fun resolve(automation: AutomationAggregate): Mono<ResolvedAutomation> {
        return Mono.zip(
            resolveList(automation.triggers.values),
            resolveCondition(automation.condition),
            resolveList(automation.actions.values)
        )
            .map { tuple ->
                ResolvedAutomation(
                    automationId = automation.automationId,
                    premisesId = automation.premisesId,
                    name = automation.name,
                    description = automation.description,
                    triggers = tuple.t1,
                    condition = tuple.t2,
                    actions = tuple.t3,
                    state = automation.state,
                    executionMode = automation.executionMode,
                    createdOn = automation.createdOn,
                    expiresOn = automation.expiresOn,
                    updatedOn = automation.updatedOn,
                )
            }
    }

    private fun <C : Component, D : ComponentData<C>> resolveList(values: List<C>): Mono<List<D>> {
        return createFlux(values)
            .flatMap { component -> resolverStrategyFactory.resolve<C, D>(component) }
            .collectList()
    }

    private fun resolveCondition(specification: Specification<Condition>?): Mono<Specification<ComponentData<Condition>>?> {
        if (specification == null) return createMono(null)
        return resolveConditionNonNull(specification).map { it }
    }

    private fun resolveConditionNonNull(specification: Specification<Condition>): Mono<Specification<ComponentData<Condition>>> {
        return when (specification) {
            is AndSpecification -> {
                Flux.fromIterable(specification.specifications)
                    .flatMap { resolveConditionNonNull(it) }
                    .collectList()
                    .map { resolved -> AndSpecification(resolved) }
            }

            is OrSpecification -> {
                Flux.fromIterable(specification.specifications)
                    .flatMap { resolveConditionNonNull(it) }
                    .collectList()
                    .map { resolved -> OrSpecification(resolved) }
            }

            is NotSpecification -> {
                resolveConditionNonNull(specification.specification)
                    .map { resolvedInner -> NotSpecification(resolvedInner) }
            }

            else -> {
                resolverStrategyFactory.resolve(specification as Condition)
                    .map { resolved -> ConditionSpecificationData(resolved) }
            }
        }
    }
}