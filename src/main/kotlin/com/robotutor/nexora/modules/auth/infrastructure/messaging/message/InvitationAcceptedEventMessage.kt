package com.robotutor.nexora.modules.auth.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class InvitationAcceptedEventMessage(val invitationId: String) : EventMessage
