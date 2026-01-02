package com.robotutor.nexora.module.device.application.facade

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.domain.vo.FeedId
import reactor.core.publisher.Flux

interface FeedFacade {
    fun registerFeeds(deviceId: DeviceId, modelNo: ModelNo): Flux<FeedId>
}
