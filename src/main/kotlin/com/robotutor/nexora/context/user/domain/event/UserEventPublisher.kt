package com.robotutor.nexora.context.user.domain.event

import com.robotutor.nexora.shared.domain.event.EventPublisher

interface UserEventPublisher : EventPublisher<UserEvent>
