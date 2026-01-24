package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator
import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class FeedControlResponse(
    val feedId: String,
    val value: Int,
    val operator: ComparisonOperator
) : ComponentResponse(ComponentType.FEED_CONTROL)
