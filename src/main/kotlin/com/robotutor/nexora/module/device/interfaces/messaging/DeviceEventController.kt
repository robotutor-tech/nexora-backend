package com.robotutor.nexora.module.device.interfaces.messaging

import com.robotutor.nexora.module.device.application.service.ActivateDeviceService
import com.robotutor.nexora.module.device.application.service.CompensateDeviceService
import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.interfaces.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.module.device.interfaces.messaging.message.ActorRegisteredDeviceMessage
import com.robotutor.nexora.module.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.common.message.annotation.EventController
import com.robotutor.nexora.common.message.annotation.EventListener
import com.robotutor.nexora.common.message.annotation.Message
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class DeviceEventController(
    private val compensateDeviceService: CompensateDeviceService,
    private val actorRegisteredDeviceService: ActivateDeviceService
) {

    @EventListener(["iam.account.registered.device"])
    fun activateDevice(@Message message: ActorRegisteredDeviceMessage, actorData: ActorData): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toActorRegisteredDeviceCommand(message, actorData)
        return actorRegisteredDeviceService.execute(command)
    }

    @EventListener(["iam.account.registration.failed.device"])
    fun compensateDevice(@Message message: CompensateDeviceMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCompensateDeviceCommand(message)
        return compensateDeviceService.execute(command)
    }
}