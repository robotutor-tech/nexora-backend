package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class FeedValueResponse(
    val feedId: String,
    val value: Int
) : ComponentResponse(ComponentType.FEED_VALUE)

