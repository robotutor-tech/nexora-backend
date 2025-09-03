package com.robotutor.nexora.modules.user.domain.event

import com.robotutor.nexora.shared.domain.model.UserId

data class UserRegisteredEvent(val userId: UserId) : UserEvent("user.registered")


