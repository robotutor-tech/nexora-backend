package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.application.command.CreateDeviceCommand
import com.robotutor.nexora.modules.device.application.event.DeviceEventPublisher
import com.robotutor.nexora.modules.device.application.facade.ActorFacade
import com.robotutor.nexora.modules.device.application.facade.TokenFacade
import com.robotutor.nexora.modules.device.application.facade.dto.DeviceTokens
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.IdType
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.DeviceData
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
    private val actorFacade: ActorFacade,
    private val tokenFacade: TokenFacade,
    private val eventPublisher: DeviceEventPublisher
) {
    private val logger = Logger(this::class.java)

    fun register(createDeviceCommand: CreateDeviceCommand, invitationData: InvitationData): Mono<DeviceTokens> {
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
            .flatMap { device ->
                actorFacade.registerDeviceActor(device)
                    .flatMap { actorData ->
                        tokenFacade.generateDeviceToken(actorData)
                            .publishEvents(eventPublisher, device)
                            .contextWrite {
                                it.put(ActorData::class.java, actorData)
                                    .put(DeviceData::class.java, actorData.principal)
                            }
                    }
            }
            .logOnSuccess(logger, "Successfully registered new Device")
            .logOnError(logger, "", "Failed to register new Device")
    }
}