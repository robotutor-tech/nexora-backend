package com.robotutor.nexora.module.feed.application.command

import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.Resources

data class GetFeedsQuery(val actorId: ActorId, val resources: Resources)
data class GetFeedQuery(val feedId: FeedId, val premisesId: PremisesId)

