package com.robotutor.nexora.module.device.interfaces.messaging

import com.robotutor.nexora.module.device.application.service.ActivateDeviceService
import com.robotutor.nexora.module.device.application.service.CompensateDeviceService
import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.interfaces.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.module.device.interfaces.messaging.message.ActorRegisteredDeviceMessage
import com.robotutor.nexora.module.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.common.messaging.annotation.KafkaController
import com.robotutor.nexora.common.messaging.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class DeviceEventController(
    private val compensateDeviceService: CompensateDeviceService,
    private val actorRegisteredDeviceService: ActivateDeviceService
) {

    @KafkaEventListener(["iam.account.registered.device"])
    fun activateDevice(@KafkaEvent eventMessage: ActorRegisteredDeviceMessage, actorData: ActorData): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toActorRegisteredDeviceCommand(eventMessage, actorData)
        return actorRegisteredDeviceService.execute(command)
    }

    @KafkaEventListener(["iam.account.registration.failed.device"])
    fun compensateDevice(@KafkaEvent eventMessage: CompensateDeviceMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCompensateDeviceCommand(eventMessage)
        return compensateDeviceService.execute(command)
    }
}