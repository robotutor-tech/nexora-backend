package com.robotutor.nexora.shared.outbox.persistence.document

import java.time.Instant

data class MessageContextDocument(
    val eventName: String,
    val correlationId: String,
    val principalData: PrincipalDataDocument?,
    val eventId: String,
    val occurredAt: Instant,
)
