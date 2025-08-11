package com.robotutor.nexora.modules.device.domain.model

import com.robotutor.nexora.shared.domain.model.FeedId

class FeedIds(private val feeds: List<FeedId>) {
    fun asList(): List<FeedId> = feeds
}