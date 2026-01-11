package com.robotutor.nexora.module.automation.domain.vo.component.data

import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedControlData(
    val feedId: FeedId,
    val value: Int
) : ComponentData<FeedControl>