package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator

data class FeedControlConfigRequest(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ConfigRequest(ConfigType.FEED_CONTROL)