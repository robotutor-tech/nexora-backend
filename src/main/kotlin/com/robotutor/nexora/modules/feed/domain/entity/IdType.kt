package com.robotutor.nexora.modules.feed.domain.entity

import com.robotutor.nexora.shared.domain.model.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    FEED_ID(12),
}