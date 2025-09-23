package com.robotutor.nexora.modules.auth.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.auth.domain.event.AuthEvent
import com.robotutor.nexora.modules.auth.domain.event.AuthUserRegisteredEvent
import com.robotutor.nexora.modules.auth.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.modules.auth.infrastructure.messaging.message.AuthUserRegisteredEventMessage
import com.robotutor.nexora.modules.auth.infrastructure.messaging.message.InvitationAcceptedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage

object AuthEventMapper : EventMapper<AuthEvent> {
    override fun toEventMessage(event: AuthEvent): EventMessage {
        return when (event) {
            is AuthUserRegisteredEvent -> toAuthUserRegisteredEventMessage(event)
            is InvitationAcceptedEvent -> toInvitationAcceptedEventMessage(event)
        }
    }

    private fun toInvitationAcceptedEventMessage(event: InvitationAcceptedEvent): InvitationAcceptedEventMessage {
        return InvitationAcceptedEventMessage(event.invitationId.value)
    }

    private fun toAuthUserRegisteredEventMessage(event: AuthUserRegisteredEvent): AuthUserRegisteredEventMessage {
        return AuthUserRegisteredEventMessage(event.userId.value)
    }
}