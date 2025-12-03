package com.robotutor.nexora.context.user.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.model.UserId

sealed class UserEvent(name: String) : DomainEvent("user.$name")

data class UserRegisteredEvent(val userId: UserId) : UserEvent("user.registered")