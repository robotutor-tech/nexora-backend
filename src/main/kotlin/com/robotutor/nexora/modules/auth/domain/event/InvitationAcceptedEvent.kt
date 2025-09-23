package com.robotutor.nexora.modules.auth.domain.event

import com.robotutor.nexora.shared.domain.model.InvitationId

data class InvitationAcceptedEvent(val invitationId: InvitationId) : AuthEvent("invitation.accepted")
