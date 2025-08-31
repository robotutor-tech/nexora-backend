package com.robotutor.nexora.modules.auth.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.UserId

data class AuthUserRegisteredEvent(val userId: UserId) : DomainEvent("auth.auth-user.registered")
