package com.robotutor.nexora.modules.feed.models

import com.robotutor.nexora.common.security.service.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    FEED_ID(12),
}
