package com.robotutor.nexora.modules.automation.application.validation

import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConfigValidation(private val ruleConfigValidationStrategyFactory: ConfigValidationStrategyFactory) {
    fun validate(config: Config, actorData: ActorData): Mono<Config> {
        val configValidatorStrategy = ruleConfigValidationStrategyFactory.getStrategy(config)
        return configValidatorStrategy.validate(config, actorData)
    }
}