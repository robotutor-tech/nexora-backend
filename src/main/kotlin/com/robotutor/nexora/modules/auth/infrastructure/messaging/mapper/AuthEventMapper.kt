package com.robotutor.nexora.modules.auth.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.auth.domain.event.AuthEvent
import com.robotutor.nexora.modules.auth.domain.event.AuthUserRegisteredEvent
import com.robotutor.nexora.modules.auth.infrastructure.messaging.message.AuthUserRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Service

@Service
class AuthEventMapper : EventMapper<AuthEvent> {
    override fun toEventMessage(event: AuthEvent): EventMessage {
        return when (event) {
            is AuthUserRegisteredEvent -> toAuthUserRegisteredEventMessage(event)
        }
    }

    private fun toAuthUserRegisteredEventMessage(event: AuthUserRegisteredEvent): AuthUserRegisteredEventMessage {
        return AuthUserRegisteredEventMessage(event.userId.value)
    }
}