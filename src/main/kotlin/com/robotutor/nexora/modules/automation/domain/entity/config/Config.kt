package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.model.FeedId

sealed interface Config
sealed interface ActionConfig : Config
sealed interface ConditionConfig : Config
sealed interface TriggerConfig : Config
sealed interface FeedConfig : Config


data class FeedControlConfig(
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Int
) : FeedConfig, ConditionConfig, TriggerConfig
