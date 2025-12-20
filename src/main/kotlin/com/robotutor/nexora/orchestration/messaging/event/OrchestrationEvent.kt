package com.robotutor.nexora.orchestration.messaging.event

import com.robotutor.nexora.shared.domain.DomainEvent

sealed interface OrchestrationEvent : DomainEvent
data class CompensateAccountRegistrationEvent(val accountId: String) : OrchestrationEvent
