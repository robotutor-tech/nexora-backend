package com.robotutor.nexora.modules.feed.application.command

import com.robotutor.nexora.shared.domain.model.FeedId

data class FeedValueUpdateCommand(
    val feedId: FeedId,
    val value: Int
)
