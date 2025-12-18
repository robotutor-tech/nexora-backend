package com.robotutor.nexora.context.device.interfaces.messaging

import com.robotutor.nexora.context.device.application.usecase.CommissionDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.CompensateDeviceRegistrationUseCase
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.interfaces.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.context.device.interfaces.messaging.message.CompensateDeviceRegistrationMessage
import com.robotutor.nexora.context.device.interfaces.messaging.message.DeviceAccountCreatedMessage
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class DeviceEventController(
    private val compensateDeviceRegistrationUseCase: CompensateDeviceRegistrationUseCase,
    private val commissionDeviceUseCase: CommissionDeviceUseCase
) {

    @KafkaEventListener(["iam.account.created.machine"])
    fun activateUser(@KafkaEvent eventMessage: DeviceAccountCreatedMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCommissionDeviceCommand(eventMessage)
        return commissionDeviceUseCase.execute(command)
    }

    @KafkaEventListener(["orchestration.compensate.device-registration"])
    fun deviceFeedsUpdate(@KafkaEvent eventMessage: CompensateDeviceRegistrationMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCompensateDeviceRegistrationCommand(eventMessage)
        return compensateDeviceRegistrationUseCase.execute(command)
    }
}