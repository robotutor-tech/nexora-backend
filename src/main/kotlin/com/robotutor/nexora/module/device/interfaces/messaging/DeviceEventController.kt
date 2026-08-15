package com.robotutor.nexora.module.device.interfaces.messaging

import com.robotutor.nexora.module.device.application.service.ActivateDeviceService
import com.robotutor.nexora.module.device.application.service.CompensateDeviceService
import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.interfaces.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.module.device.interfaces.messaging.message.ActorRegisteredDeviceMessage
import com.robotutor.nexora.module.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.message.annotation.EventController
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.annotation.Message
import com.robotutor.nexora.shared.message.config.EventName
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class DeviceEventController(
    private val compensateDeviceService: CompensateDeviceService,
    private val actorRegisteredDeviceService: ActivateDeviceService
) {

    @EventListener([EventName.IDENTITY_ACCOUNT_REGISTERED_DEVICE])
    fun activateDevice(@Message message: ActorRegisteredDeviceMessage, actorData: ActorData): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toActorRegisteredDeviceCommand(message, actorData)
        return actorRegisteredDeviceService.execute(command)
    }

    @EventListener([EventName.IDENTITY_ACCOUNT_REGISTRATION_FAILED_DEVICE])
    fun compensateDevice(@Message message: CompensateDeviceMessage): Mono<DeviceAggregate> {
        val command = DeviceEventMapper.toCompensateDeviceCommand(message)
        return compensateDeviceService.execute(command)
    }
}
