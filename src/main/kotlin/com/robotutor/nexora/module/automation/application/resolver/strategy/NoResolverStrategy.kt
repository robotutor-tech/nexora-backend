package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.application.resolver.ResolverStrategy
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NoResolverStrategy : ResolverStrategy<Component, ComponentData<Component>> {
    override fun resolve(component: Component): Mono<ComponentData<Component>> {
        @Suppress("UNCHECKED_CAST")
        return createMono(component) as Mono<ComponentData<Component>>
    }
}