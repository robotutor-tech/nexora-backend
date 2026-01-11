package com.robotutor.nexora.module.automation.domain.vo.component.data

import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedValueData(
    val feedId: FeedId,
    val value: Int,
) : ComponentData<FeedValue>