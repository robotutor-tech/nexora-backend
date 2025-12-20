package com.robotutor.nexora.orchestration.messaging.event

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class OrchestrationEventMessage(name: String) : EventMessage("orchestration.$name")

data class CompensateAccountRegistrationEventMessage(val accountId: String) :
    OrchestrationEventMessage("compensate.account-registration")

data class CompensatePremisesRegistrationEventMessage(val premisesId: String) :
    OrchestrationEventMessage("compensate.premises-registration")
