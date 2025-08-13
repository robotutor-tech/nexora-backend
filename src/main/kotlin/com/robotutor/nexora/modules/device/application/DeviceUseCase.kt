package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.application.command.CreateDeviceCommand
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.modules.device.domain.model.IdType
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val idGeneratorService: IdGeneratorService
) {
    private val logger = Logger(this::class.java)

    fun register(createDeviceCommand: CreateDeviceCommand): Mono<Device> {
        return idGeneratorService.generateId(IdType.DEVICE_ID)
            .map { deviceId ->
                Device(
                    deviceId = DeviceId(deviceId),
                    premisesId = PremisesId(createDeviceCommand.premisesId),
                    name = createDeviceCommand.name,
                    modelNo = createDeviceCommand.modelNo,
                    serialNo = createDeviceCommand.serialNo,
                    type = createDeviceCommand.type,
                    feedIds = FeedIds(createDeviceCommand.feedIds.map { FeedId(it) }),
                    state = createDeviceCommand.state,
                    health = createDeviceCommand.health,
                    os = createDeviceCommand.os,
                    createdBy = ActorId(createDeviceCommand.createdBy),
                )
            }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully updated feedIds for deviceId")
            .logOnError(logger, "", "Failed to add audit message")
    }

    fun getDevices(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device> {
        return deviceRepository.findAllByPremisesIdAndDeviceIdsIn(premisesId, deviceIds)
    }

    fun getDevice(deviceId: DeviceId, premisesId: PremisesId): Mono<Device> {
        return deviceRepository.findByPremisesIdAndDeviceId(premisesId, deviceId)
    }
}