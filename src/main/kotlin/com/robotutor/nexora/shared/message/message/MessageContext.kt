package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.outbox.vo.EventId
import java.time.Instant

data class MessageContext(
    override val eventName: EventName,
    override val correlationId: String,
    override val principalData: PrincipalData?,
    override val eventId: EventId = EventId.generate(),
    override val occurredAt: Instant = Instant.now(),
) : KafkaMessage
