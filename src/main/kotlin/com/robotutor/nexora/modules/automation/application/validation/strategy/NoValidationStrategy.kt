package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NoValidationStrategy : ValidationStrategy<Config> {
    override fun validate(config: Config, actorData: ActorData): Mono<Config> {
        return createMono(config)
    }
}