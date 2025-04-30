package com.robotutor.nexora.feed.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    FEED_ID(12),
}
