package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import reactor.core.publisher.Mono

interface ComponentResolver<C : Component, D : ComponentData<C>> {
    fun resolve(component: C): Mono<D>
}

