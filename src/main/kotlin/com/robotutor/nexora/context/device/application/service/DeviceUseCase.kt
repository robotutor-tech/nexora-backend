package com.robotutor.nexora.context.device.application.service

import com.robotutor.nexora.context.device.application.command.GetDeviceQuery
import com.robotutor.nexora.context.device.application.command.GetDevicesQuery
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceState
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByRegisteredBySpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByStateSpecification
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.specification.AuthorizedQueryBuilder
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val authorizedQueryBuilder: AuthorizedQueryBuilder<DeviceId, DeviceAggregate>,

    ) {
    private val logger = Logger(this::class.java)

    @Authorize(ActionType.READ, ResourceType.DEVICE)
    fun execute(query: GetDevicesQuery): Flux<DeviceAggregate> {
        val specification = authorizedQueryBuilder.build(query.resources)
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