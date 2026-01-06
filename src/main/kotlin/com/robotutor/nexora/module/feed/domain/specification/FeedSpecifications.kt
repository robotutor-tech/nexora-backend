package com.robotutor.nexora.module.feed.domain.specification

import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId

fun feedByFeedIdAndPremisesIdSpecification(feedId: FeedId, premisesId: PremisesId) =
    FeedByFeedIdSpecification(feedId).and(FeedByPremisesIdSpecification(premisesId))


