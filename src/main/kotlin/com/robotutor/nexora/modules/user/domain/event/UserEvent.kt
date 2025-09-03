package com.robotutor.nexora.modules.user.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent

sealed class UserEvent(name: String) : DomainEvent("user.$name")