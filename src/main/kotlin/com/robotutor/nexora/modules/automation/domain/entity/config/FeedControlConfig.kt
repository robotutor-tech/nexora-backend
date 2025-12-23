package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator
import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedControlConfig(
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Int
) : TriggerConfig, ConditionConfig, RuleConfigType(ConfigType.FEED_CONTROL)

