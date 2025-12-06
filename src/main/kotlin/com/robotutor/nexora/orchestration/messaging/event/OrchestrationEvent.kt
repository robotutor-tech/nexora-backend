package com.robotutor.nexora.orchestration.messaging.event

import com.robotutor.nexora.shared.domain.DomainEvent

sealed interface OrchestrationEvent : DomainEvent

data class CompensateUserRegistrationEvent(val userId: String) : OrchestrationEvent