package com.robotutor.nexora.modules.user.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.UserId

data class UserRegisteredEvent(val userId: UserId) : DomainEvent("user.user.registered")


