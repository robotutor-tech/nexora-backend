package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.outbox.entity.OutboxEvent
import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import reactor.core.publisher.Mono

interface Recorder {
    fun record(message: OutboxEvent): Mono<OutboxDocument>
}
