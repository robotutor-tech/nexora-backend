package com.robotutor.nexora.module.feed.application.command

import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class UpdateValueCommand(
    val feedId: FeedId,
    val value: Int,
    val premisesId: PremisesId
)
