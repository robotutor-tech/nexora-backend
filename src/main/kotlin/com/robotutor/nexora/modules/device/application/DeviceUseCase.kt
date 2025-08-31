package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
) {
    private val logger = Logger(this::class.java)

    fun getDevices(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device> {
        return deviceRepository.findAllByPremisesIdAndDeviceIdsIn(premisesId, deviceIds)
    }

    fun getDevice(deviceId: DeviceId, premisesId: PremisesId): Mono<Device> {
        return deviceRepository.findByPremisesIdAndDeviceId(premisesId, deviceId)
    }

    fun updateDeviceFeeds(deviceId: DeviceId, feedIds: FeedIds, actorData: ActorData): Mono<Device> {
        return getDevice(deviceId, actorData.premisesId)
            .map { device -> device.updateFeedIds(feedIds) }
            .flatMap { device -> deviceRepository.save(device).map { device } }
//            .publishEvents()
            .logOnSuccess(logger, "Successfully updated feedIds for deviceId")
            .logOnError(logger, "", "Failed to add audit message")
    }
}