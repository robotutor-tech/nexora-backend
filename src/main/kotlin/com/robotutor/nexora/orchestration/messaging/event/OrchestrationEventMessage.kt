package com.robotutor.nexora.orchestration.messaging.event

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class OrchestrationEventMessage(name: String) : EventMessage("orchestration.$name")

data class CompensateUserRegistrationEventMessage(val userId: String) :
    OrchestrationEventMessage("compensate.user-registration")

data class CompensateDeviceRegistrationEventMessage(val deviceId: String) :
    OrchestrationEventMessage("compensate.device-registration")