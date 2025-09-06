package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.shared.domain.model.ActorData
import reactor.core.publisher.Mono

interface ValidationStrategy<T : Config> {
    fun validate(config: T, actorData: ActorData): Mono<T>
}