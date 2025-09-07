package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType
import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.ConfigResponse

data class FeedControlConfigResponse(
    val feedId: String,
    val value: Int,
    val operator: ComparisonOperator
) : ConfigResponse(ConfigType.FEED_CONTROL)
