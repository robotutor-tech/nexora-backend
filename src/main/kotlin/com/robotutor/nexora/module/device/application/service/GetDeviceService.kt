package com.robotutor.nexora.module.device.application.service

import com.robotutor.nexora.module.device.application.command.GetDeviceQuery
import com.robotutor.nexora.module.device.application.command.GetDevicesQuery
import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.domain.aggregate.DeviceState
import com.robotutor.nexora.module.device.domain.repository.DeviceRepository
import com.robotutor.nexora.module.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.module.device.domain.specification.DeviceByRegisteredBySpecification
import com.robotutor.nexora.module.device.domain.specification.DeviceByStateSpecification
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.specification.ResourceSpecificationBuilder
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetDeviceService(
    private val deviceRepository: DeviceRepository,
    private val resourceSpecificationBuilder: ResourceSpecificationBuilder<DeviceAggregate>,
) {
    private val logger = Logger(this::class.java)

    @Authorize(ActionType.READ, ResourceType.DEVICE)
    fun execute(query: GetDevicesQuery): Flux<DeviceAggregate> {
        val specification = resourceSpecificationBuilder.build(query.resources)
            .and(DeviceByPremisesIdSpecification(query.resources.premisesId))
            .and(
                DeviceByStateSpecification(DeviceState.ACTIVE)
                    .or(DeviceByStateSpecification(DeviceState.REGISTERED))
            )
            .or(
                DeviceByRegisteredBySpecification(query.actorId)
                    .and(DeviceByStateSpecification(DeviceState.CREATED))
            )

        return deviceRepository.findAll(specification)
            .logOnSuccess(logger, "Successfully get devices")
            .logOnError(logger, "Failed to get devices")
    }

    @Authorize(ActionType.READ, ResourceType.DEVICE, "#query.deviceId.value")
    fun execute(query: GetDeviceQuery): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(query.deviceId)
            .logOnSuccess(logger, "Successfully get device")
            .logOnError(logger, "Failed to get device")
    }
}