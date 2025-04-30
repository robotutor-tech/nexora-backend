package com.robotutor.nexora.device.services

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.device.models.Device
import com.robotutor.nexora.device.models.IdType
import com.robotutor.nexora.device.repositories.DeviceRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DeviceService(
    private val idGeneratorService: IdGeneratorService,
    private val deviceRepository: DeviceRepository
) {
    val logger = Logger(this::class.java)

    fun register(deviceRequest: DeviceRequest, premisesActorData: PremisesActorData): Mono<Device> {
        return idGeneratorService.generateId(IdType.DEVICE_ID)
            .flatMap { deviceId ->
                deviceRepository.save(Device.from(deviceId, deviceRequest, premisesActorData))
            }
            .logOnSuccess(logger, "Successfully registered device")
            .logOnError(logger, "", "Error registering device")
    }

}
