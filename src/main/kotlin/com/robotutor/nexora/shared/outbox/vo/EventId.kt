package com.robotutor.nexora.shared.outbox.vo

import com.robotutor.nexora.shared.domain.vo.Identifier
import java.util.*

data class EventId(override val value: String) : Identifier {
    companion object {
        fun generate(): EventId {
            return EventId(UUID.randomUUID().toString())
        }
    }
}
