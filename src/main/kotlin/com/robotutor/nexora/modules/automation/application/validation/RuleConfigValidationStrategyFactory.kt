package com.robotutor.nexora.modules.automation.application.validation

import com.robotutor.nexora.modules.automation.application.validation.strategy.AutomationConfigValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.FeedControlConfigValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.FeedValueConfigValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.NoValidationStrategy
import com.robotutor.nexora.modules.automation.application.validation.strategy.ValidationStrategy
import com.robotutor.nexora.modules.automation.domain.entity.config.*
import org.springframework.stereotype.Service

@Service
class ConfigValidationStrategyFactory(
    private val noValidationStrategy: NoValidationStrategy,
    private val feedControlConfigValidationStrategy: FeedControlConfigValidationStrategy,
    private val automationConfigValidationStrategy: AutomationConfigValidationStrategy,
    private val feedValueConfigValidationStrategy: FeedValueConfigValidationStrategy
) {
    fun getStrategy(config: Config): ValidationStrategy<Config> {
        @Suppress("UNCHECKED_CAST")
        return when (config) {
            is AutomationConfig -> automationConfigValidationStrategy
            is FeedValueConfig -> feedValueConfigValidationStrategy
            is WaitConfig -> noValidationStrategy
            is FeedControlConfig -> feedControlConfigValidationStrategy
            is TimeRangeConfig -> noValidationStrategy
            is ScheduleConfig -> noValidationStrategy
            is VoiceConfig -> noValidationStrategy
        } as ValidationStrategy<Config>
    }
}