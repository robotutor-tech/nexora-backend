package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UpdateFeedsUseCase(private val deviceRepository: DeviceRepository) {

    private val logger = Logger(this::class.java)

    fun updateFeeds(deviceId: DeviceId, feedIds: FeedIds): Mono<Device> {
        return deviceRepository.findByDeviceId(deviceId)
            .map { device -> device.updateFeedIds(feedIds) }
            .flatMap { device -> deviceRepository.save(device).map { device } }
            .logOnSuccess(logger, "Successfully updated feedIds for deviceId", mapOf("deviceId" to deviceId))
            .logOnError(logger, "", "Failed to add audit message", mapOf("deviceId" to deviceId))
    }

}