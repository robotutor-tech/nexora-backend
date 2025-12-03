package com.robotutor.nexora.modules.auth.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent

sealed class AuthEvent(name: String) : DomainEvent("auth.$name")