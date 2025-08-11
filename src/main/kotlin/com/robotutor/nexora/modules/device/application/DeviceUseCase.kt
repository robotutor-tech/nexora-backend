package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.DeviceDetails
import com.robotutor.nexora.modules.device.domain.model.IdType
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val idGeneratorService: IdGeneratorService
) {
    private val logger = Logger(this::class.java)

    fun register(deviceDetails: DeviceDetails): Mono<Device> {
        return idGeneratorService.generateId(IdType.DEVICE_ID)
            .map { deviceId -> Device.from(DeviceId(deviceId), deviceDetails) }
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