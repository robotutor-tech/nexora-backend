package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator

data class FeedControlConfigResponse(
    val feedId: String,
    val value: Int,
    val operator: ComparisonOperator
) : ConfigResponse(ConfigType.FEED_CONTROL)
