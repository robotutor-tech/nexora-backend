package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType
import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator

data class FeedControlConfigRequest(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ConfigRequest(ConfigType.FEED_CONTROL)