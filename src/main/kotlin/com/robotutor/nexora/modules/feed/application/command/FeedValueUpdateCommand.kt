package com.robotutor.nexora.modules.feed.application.command

import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedValueUpdateCommand(
    val feedId: FeedId,
    val value: Int
)
