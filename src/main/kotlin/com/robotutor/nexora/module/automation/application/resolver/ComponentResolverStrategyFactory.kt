package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.application.resolver.strategy.*
import com.robotutor.nexora.module.automation.domain.vo.component.AutomationComponent
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.module.automation.domain.vo.component.Voice
import com.robotutor.nexora.module.automation.domain.vo.component.Wait
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ComponentResolverStrategyFactory(
    private val automationResolver: AutomationResolver,
    private val feedControlResolver: FeedControlResolver,
    private val feedValueResolver: FeedValueResolver,
    private val voiceResolver: VoiceResolver,
    private val waitResolver: WaitResolver
) {

    fun <C : Component, D : ComponentData<C>> resolve(component: C): Mono<D> {
        @Suppress("UNCHECKED_CAST")
        val strategy = getStrategy(component) as ComponentResolver<C, D>
        return strategy.resolve(component)
    }

    private fun getStrategy(component: Component): ComponentResolver<out Component, out ComponentData<out Component>> {
        return when (component) {
            is AutomationComponent -> automationResolver
            is FeedValue -> feedValueResolver
            is Wait -> waitResolver
            is FeedControl -> feedControlResolver
            is Voice -> voiceResolver
        }
    }
}
