package com.robotutor.nexora.context.device.interfaces.messaging

import com.robotutor.nexora.context.device.application.usecase.ActivateDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.CompensateDeviceUseCase
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.interfaces.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.context.device.interfaces.messaging.message.ActivateDeviceMessage
import com.robotutor.nexora.context.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaController
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class DeviceEventController(
    private val compensateDeviceUseCase: CompensateDeviceUseCase,
    private val activateDeviceUseCase: ActivateDeviceUseCase
) {

    @KafkaEventListener(["iam.account.registered.device"])
    fun activateDevice(@KafkaEvent eventMessage: ActivateDeviceMessage, actorData: ActorData): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toActivateDeviceCommand(eventMessage, actorData)
        return activateDeviceUseCase.execute(command)
    }

    @KafkaEventListener(["iam.account.registration.failed.device"])
    fun compensateDevice(@KafkaEvent eventMessage: CompensateDeviceMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCompensateDeviceCommand(eventMessage)
        return compensateDeviceUseCase.execute(command)
    }
}