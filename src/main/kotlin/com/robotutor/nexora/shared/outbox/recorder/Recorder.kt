package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import reactor.core.publisher.Mono

interface Recorder {
    fun record(message: EventMessage): Mono<OutboxDocument>
}
