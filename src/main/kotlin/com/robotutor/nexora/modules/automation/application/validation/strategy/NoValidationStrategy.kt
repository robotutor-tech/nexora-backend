package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NoValidationStrategy : ValidationStrategy<Config> {
    override fun validate(config: Config, actorData: ActorData): Mono<Config> {
        return createMono(config)
    }
}