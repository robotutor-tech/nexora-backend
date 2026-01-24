package com.robotutor.nexora.module.automation.interfaces.controller.view.component.request

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator
import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class FeedControlRequest(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ComponentRequest(ComponentType.FEED_CONTROL)