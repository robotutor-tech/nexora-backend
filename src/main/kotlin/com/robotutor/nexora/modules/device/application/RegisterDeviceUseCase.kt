package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.application.command.CreateDeviceCommand
import com.robotutor.nexora.modules.device.application.facade.AuthDeviceFacade
import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.modules.device.domain.entity.IdType
import com.robotutor.nexora.modules.device.domain.event.DeviceEvent
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val idGeneratorService: IdGeneratorService,
    private val authDeviceFacade: AuthDeviceFacade,
    private val eventPublisher: EventPublisher<DeviceEvent>,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>
) {
    private val logger = Logger(this::class.java)

    fun register(createDeviceCommand: CreateDeviceCommand, invitationData: InvitationData): Mono<Device> {
        return idGeneratorService.generateId(IdType.DEVICE_ID, DeviceId::class.java)
            .map { deviceId ->
                Device.create(
                    deviceId = deviceId,
                    premisesId = invitationData.premisesId,
                    name = invitationData.name,
                    modelNo = createDeviceCommand.modelNo,
                    serialNo = createDeviceCommand.serialNo,
                    type = createDeviceCommand.type,
                    createdBy = invitationData.invitedBy,
                    zoneId = invitationData.zoneId,
                )
            }
            .flatMap { device -> deviceRepository.save(device).map { device } }
//            .flatMap { device ->
//                actorFacade.registerDeviceActor(device)
//                    .flatMap { actorData ->
////                        val resourceCreatedEvent = ResourceCreatedEvent(
////                            resourceType = ResourceType.DEVICE,
////                            resourceId = ResourceId(device.deviceId.value)
////                        )
//                        authDeviceFacade.register(device, actorData)
////                            .publishEvents(eventPublisher, device)
////                            .publishEvent(resourceCreatedEventPublisher, resourceCreatedEvent)
//                            .contextWrite {
//                                it.put(ActorData::class.java, actorData)
//                                    .put(DeviceData::class.java, actorData.principal)
//                            }
//                    }
//            }
            .logOnSuccess(logger, "Successfully registered new Device")
            .logOnError(logger, "Failed to register new Device")
    }
}