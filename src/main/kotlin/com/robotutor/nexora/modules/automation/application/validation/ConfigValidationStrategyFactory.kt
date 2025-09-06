package com.robotutor.nexora.modules.automation.application.validation

import com.robotutor.nexora.modules.automation.application.validation.strategy.AutomationConfigValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.FeedConfigValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.NoValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.ValidationStrategy
import com.robotutor.nexora.modules.automation.domain.entity.config.*
import org.springframework.stereotype.Service

@Service
class ConfigValidationStrategyFactory(
    private val noValidationStrategy: NoValidationStrategy,
    private val feedConfigValidationStrategy: FeedConfigValidationStrategy,
    private val automationConfigValidationStrategy: AutomationConfigValidationStrategy
) {
    fun getStrategy(config: Config): ValidationStrategy<Config> {
        @Suppress("UNCHECKED_CAST")
        return when (config) {
            is AutomationConfig -> automationConfigValidationStrategy
            is FeedValueConfig -> feedConfigValidationStrategy
            is WaitConfig -> noValidationStrategy
            is FeedControlConfig -> feedConfigValidationStrategy
            is TimeRangeConfig -> noValidationStrategy
            is ScheduleTriggerConfig -> noValidationStrategy
            is VoiceConfig -> noValidationStrategy
            is SunConfig -> noValidationStrategy
            is TimeConfig -> noValidationStrategy
        } as ValidationStrategy<Config>
    }
}