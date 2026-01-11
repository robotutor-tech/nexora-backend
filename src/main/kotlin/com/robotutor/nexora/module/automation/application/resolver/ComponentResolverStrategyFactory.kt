package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.application.resolver.strategy.AutomationResolverStrategy
import com.robotutor.nexora.module.automation.application.resolver.strategy.FeedControlResolverStrategy
import com.robotutor.nexora.module.automation.application.resolver.strategy.FeedValueResolverStrategy
import com.robotutor.nexora.module.automation.application.resolver.strategy.NoResolverStrategy
import com.robotutor.nexora.module.automation.domain.vo.component.*
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import org.springframework.stereotype.Service

@Service
class ComponentResolverStrategyFactory(
    private val automationResolverStrategy: AutomationResolverStrategy,
    private val feedValueResolverStrategy: FeedValueResolverStrategy,
    private val feedControlResolverStrategy: FeedControlResolverStrategy,
    private val noResolverStrategy: NoResolverStrategy
) {
    fun <C : Component, D : ComponentData<C>> getStrategy(component: C): ResolverStrategy<C, D> {
        @Suppress("UNCHECKED_CAST")
        return when (component) {
            is FeedValue -> feedValueResolverStrategy
            is Wait -> noResolverStrategy
            is FeedControl -> feedControlResolverStrategy
            is Voice -> noResolverStrategy
            is Automation -> automationResolverStrategy
            else -> throw IllegalArgumentException("No resolver strategy found for component type: ${component::class.java}")
        } as ResolverStrategy<C, D>
    }
}