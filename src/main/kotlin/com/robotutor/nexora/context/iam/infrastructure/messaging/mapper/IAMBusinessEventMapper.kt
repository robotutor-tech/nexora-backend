package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.AccountCompensatedEvent
import com.robotutor.nexora.context.iam.domain.event.ActorAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMBusinessEvent
import com.robotutor.nexora.context.iam.domain.event.PremisesResourceCreatedEvent
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountAuthenticatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountCompensatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.ActorAuthenticatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.PremisesResourceCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMBusinessEventMapper : EventMapper<IAMBusinessEvent> {
    override fun toEventMessage(event: IAMBusinessEvent): EventMessage {
        return when (event) {
            is AccountAuthenticatedEvent -> toAccountAuthenticatedEventMessage(event)
            is ActorAuthenticatedEvent -> toActorAuthenticatedEventMessage(event)
            is AccountCompensatedEvent -> toAccountCompensatedEventMessage(event)
            is PremisesResourceCreatedEvent -> toPremisesResourceCreatedEventMessage(event)
        }
    }

    private fun toPremisesResourceCreatedEventMessage(event: PremisesResourceCreatedEvent): PremisesResourceCreatedEventMessage {
        return PremisesResourceCreatedEventMessage(event.premisesId.value, event.ownerAccountId.value)
    }

    private fun toActorAuthenticatedEventMessage(event: ActorAuthenticatedEvent): ActorAuthenticatedEventMessage {
        return ActorAuthenticatedEventMessage(
            event.accountId.value,
            event.type.name,
            event.actorId.value,
            event.premisesId.value
        )
    }

    private fun toAccountAuthenticatedEventMessage(event: AccountAuthenticatedEvent): AccountAuthenticatedEventMessage {
        return AccountAuthenticatedEventMessage(event.accountId.value, event.type.name)
    }

    private fun toAccountCompensatedEventMessage(event: AccountCompensatedEvent): AccountCompensatedEventMessage {
        return AccountCompensatedEventMessage(event.accountId.value)
    }
}