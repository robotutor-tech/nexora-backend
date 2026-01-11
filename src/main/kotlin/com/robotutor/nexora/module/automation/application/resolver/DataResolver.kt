package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.Trigger
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.utility.createFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DataResolver(
    private val dataResolverStrategyFactory: ComponentResolverStrategyFactory
) {
    fun resolve(automation: AutomationAggregate): Mono<ResolvedAutomation> {
        return resolveComponents(automation.triggers, automation.actions, automation.condition)
            .map {
                ResolvedAutomation(
                    automationId = automation.automationId,
                    premisesId = automation.premisesId,
                    name = automation.name,
                    description = automation.description,
                    triggers = it.triggers,
                    condition = it.condition,
                    actions = it.actions,
                    state = automation.state,
                    executionMode = automation.executionMode,
                    createdOn = automation.createdOn,
                    expiresOn = automation.expiresOn,
                    updatedOn = automation.updatedOn,
                )
            }
    }

    private fun <C : Component, D : ComponentData<C>> resolveComponent(component: C): Mono<D> {
        val strategy = dataResolverStrategyFactory.getStrategy<C, D>(component)
        return strategy.resolve(component)
    }

    private fun <C : Component, D : ComponentData<C>> resolveAll(components: List<C>): Mono<List<D>> {
        return createFlux(components)
            .flatMap { component -> resolveComponent<C, D>(component) }
            .collectList()
    }

    private fun resolveComponents(
        triggers: Triggers,
        actions: Actions,
        condition: Specification<Condition>?
    ): Mono<ResolvedComponents> {
        val triggersMono = resolveAll(triggers.values)
        val actionsMono = resolveAll(actions.values)
        val conditionMono = condition?.let { resolve(condition) }
        return Mono.zip(triggersMono, conditionMono, actionsMono)
            .map { ResolvedComponents(it.t1, it.t2, it.t3) }
    }

    fun <C : Condition, D : ComponentData<C>> resolve(specification: Specification<C>): Mono<Specification<D>> {
        return when (specification) {
            is AndSpecification ->
                createFlux(specification.specifications)
                    .flatMap { resolve<C, D>(it) }
                    .collectList()
                    .map { resolved -> AndSpecification(resolved) }

            is OrSpecification ->
                createFlux(specification.specifications)
                    .flatMap { resolve<C, D>(it) }
                    .collectList()
                    .map { resolved -> OrSpecification(resolved) }

            is NotSpecification -> resolve<C, D>(specification.specification)
                .map { resolvedInner -> NotSpecification(resolvedInner) }

            else -> resolveComponent<C, D>(specification as C) as Mono<Specification<D>>
        }
    }


}

private data class ResolvedComponents(
    val triggers: List<ComponentData<Trigger>>,
    val condition: Specification<ComponentData<Condition>>,
    val actions: List<ComponentData<Action>>,
)