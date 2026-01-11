package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import reactor.core.publisher.Mono

interface ResolverStrategy<T : Component, D : ComponentData<T>> {
    fun resolve(component: T): Mono<D>
}